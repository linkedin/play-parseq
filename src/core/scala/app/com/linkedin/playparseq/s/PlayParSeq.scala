/*
 * Copyright 2015 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.linkedin.playparseq.s

import com.linkedin.parseq.{Engine, Task}
import com.linkedin.parseq.promise.Promises
import com.linkedin.playparseq.s.PlayParSeqImplicits._
import com.linkedin.playparseq.s.stores.ParSeqTaskStore
import com.linkedin.playparseq.utils.PlayParSeqHelper
import javax.inject.{Inject, Singleton}
import play.api.mvc.RequestHeader
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}


/**
 * The trait PlayParSeq defines the conversions from a function `() => Future[T]` to a ParSeq `Task[T]`, and also the
 * execution of a ParSeq `Task[T]` which returns a `Future[T]`, in the mean time putting Tasks into store.
 * Note that, in general you shouldn't be running multiple ParSeq Tasks, otherwise the order of execution might not be
 * accurate, which minimizes the benefits of ParSeq.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
trait PlayParSeq {

  /**
   * The method toTask converts a function `() => Future[T]` to a ParSeq `Task[T]`.
   *
   * @param name The String which describes the Task and shows up in a trace
   * @param f The function which returns a Future
   * @tparam T The type parameter of the Future and the ParSeq Task
   * @return The ParSeq Task
   */
  def toTask[T](name: String, f: () => Future[T]): Task[T]

  /**
   * The method toTask converts a function `() => Future[T]` to a ParSeq `Task[T]`, which binds with a default name.
   *
   * @param f The function which returns a Future
   * @tparam T The type parameter of the Future and the ParSeq Task
   * @return The ParSeq Task
   */
  def toTask[T](f: () => Future[T]): Task[T]

  /**
   * The method runTask executes a ParSeq `Task[T]` then generates a `Future[T]`, and puts into the store.
   *
   * @param task The ParSeq Task
   * @param requestHeader The request
   * @tparam T The type parameter of the ParSeq Task and the Future
   * @return The Future
   */
  def runTask[T](task: Task[T])(implicit requestHeader: RequestHeader): Future[T]

}

/**
 * The class PlayParSeqImpl is an implementation of the trait [[PlayParSeq]] with the help from the class
 * [[PlayParSeqHelper]].
 *
 * @param engine The injected ParSeq Engine component
 * @param parSeqTaskStore The injected [[ParSeqTaskStore]] component
 * @param executionContext The injected [[ExecutionContext]] component
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
class PlayParSeqImpl @Inject()(engine: Engine, parSeqTaskStore: ParSeqTaskStore)(implicit executionContext: ExecutionContext) extends PlayParSeqHelper with PlayParSeq {

  /**
   * The field DefaultTaskName is the default name of ParSeq Task.
   */
  val DefaultTaskName = "fromScalaFuture"

  /**
   * @inheritdoc
   */
  override def toTask[T](name: String, f: () => Future[T]): Task[T] = {
    // Bind a Task to the Future for both success and failure
    Task.async[T](name, {
      val promise = Promises.settable[T]()
      f().onComplete {
        case Failure(throwable) => promise.fail(throwable)
        case Success(result) => promise.done(result)
      }
      promise
    })
  }

  /**
   * @inheritdoc
   */
  override def toTask[T](f: () => Future[T]): Task[T] = {
    toTask(DefaultTaskName, f)
  }

  /**
   * @inheritdoc
   */
  override def runTask[T](task: Task[T])(implicit requestHeader: RequestHeader): Future[T] = {
    // Bind a Future to the ParSeq Task
    val future: Future[T] = bindTaskToFuture(task)
    // Put the ParSeq Task into store
    parSeqTaskStore.put(task)
    // Run the ParSeq Task
    engine.run(task)
    // Return the Future
    future
  }

}
