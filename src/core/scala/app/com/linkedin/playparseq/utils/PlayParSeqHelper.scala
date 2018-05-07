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
package com.linkedin.playparseq.utils

import com.linkedin.parseq.Task
import com.linkedin.parseq.promise.{Promises, SettablePromise, Promise => ParSeqPromise}
import java.util.concurrent.Callable

import com.linkedin.parseq.function.Try
import com.linkedin.playparseq.s.PlayParSeqImplicits
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}


/**
 * The class PlayParSeqHelper provides bindings between a `Future[T]` and a ParSeq `Task[T]`.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
private[playparseq] abstract class PlayParSeqHelper {

  /**
   * The method bindFutureToTask binds a function `() => Future[T]` to a ParSeq `Task[T]`, for both success and failure.
   *
   * @param name The String which describes the Task and shows up in a trace
   * @param f The function which returns a Future
   * @tparam T The type parameter of the Future and the ParSeq Task
   * @return The ParSeq Task
   */
  private[playparseq] def bindFutureToTask[T](name: String, f: () => Future[T]): Task[T] = {
    // Create an asynchronous ParSeq Task
    Task.async[T](name, new Callable[ParSeqPromise[_ <: T]] {
      override def call(): ParSeqPromise[_ <: T] = {
        // Create a binding ParSeq Promise
        val parSeqPromise: SettablePromise[T] = Promises.settable[T]()
        // Get the Future
        val scalaFuture: Future[T] = f()
        // Bind
        scalaFuture.onComplete {
          case Failure(t) => parSeqPromise.fail(t)
          case Success(r) => parSeqPromise.done(r)
        }
        // Return the ParSeq Promise
        parSeqPromise
      }
    })
  }

  /**
   * The method bindTaskToPromise binds a `Promise[T]` to a ParSeq `Task[T]`, for both success and failure.
   *
   * @param task The ParSeq Task
   * @param scalaPromise An uncompleted promise
   * @tparam T The type parameter of the ParSeq Task and the Promise
   * @return The Task wrapped so that the Promise will be completed when the task finishes
   */
  private[playparseq] def bindTaskToPromise[T](task: Task[T], scalaPromise: Promise[T]): Task[T] = {
    def unwrapTry(tried: Try[T]): T = {
      if (tried.isFailed) {
        val thrown = tried.getError
        scalaPromise.failure(thrown)
        throw thrown
      } else {
        val result = tried.get()
        scalaPromise.success(result)
        result
      }
    }
    task.toTry.map(PlayParSeqImplicits.toFunction1(unwrapTry))
  }
}
