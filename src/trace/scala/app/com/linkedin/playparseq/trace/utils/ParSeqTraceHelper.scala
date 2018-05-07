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
package com.linkedin.playparseq.trace.utils

import com.linkedin.parseq.Task
import com.linkedin.parseq.promise.PromiseListener
import com.linkedin.playparseq.utils.PlayParSeqHelper
import play.api.libs.iteratee.Iteratee
import play.api.mvc.Result

import scala.concurrent.Future
import scala.concurrent.Promise


/**
 * The class ParSeqTraceHelper provides consuming for `Result` together with the help from the class
 * [[PlayParSeqHelper]].
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
private[playparseq] abstract class ParSeqTraceHelper extends PlayParSeqHelper {

  /**
   * The method consumeResult consumes the `Result` body with an `Iteratee`.
   *
   * @param result The Result
   * @return The Result
   */
  private[playparseq] def consumeResult(result: Result): Future[Any] = result.body.run(Iteratee.skipToEof)


  /**
   * This is not the correct way to bind to a Future since the PromiseListener could be called by a non-ParSeq thread and
   * ThreadLocal context from the ParSeq Task thread would be lost. In this case since the Tasks can't be wrapped and
   * resubmitted to the Engine, and this is only used for trace visualization we are fine with this limitation
   */
  protected def bindTaskToFuture[T](task: Task[T]): Future[T] = {
    import com.linkedin.parseq.promise.{Promise => ParSeqPromise}
    // Create a Promise for extracting Future
    val scalaPromise: Promise[T] = Promise[T]()
    // Bind
    task.addListener(new PromiseListener[T] {
      override def onResolved(parSeqPromise: ParSeqPromise[T]) = {
        if (parSeqPromise.isFailed) scalaPromise.failure(parSeqPromise.getError)
        else scalaPromise.success(parSeqPromise.get)
      }
    })
    // Return the Future
    scalaPromise.future
  }
}
