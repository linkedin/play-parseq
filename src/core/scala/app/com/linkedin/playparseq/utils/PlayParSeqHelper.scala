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
import com.linkedin.parseq.promise.{Promise => ParSeqPromise, PromiseListener}
import java.util.concurrent.{CompletableFuture, CompletionStage}
import scala.concurrent.{Future, Promise}


/**
 * The class PlayParSeqHelper provides bindings between a ParSeq `Task[T]` to a `Future[T]` or a `CompletionStage[T]`.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
private[playparseq] abstract class PlayParSeqHelper {

  /**
   * The method bindTaskToFuture binds a `Future[T]` to a ParSeq `Task[T]`, for both success and failure.
   *
   * @param task The ParSeq Task
   * @tparam T The type parameter of the ParSeq Task and the Future
   * @return The Future
   */
  private[playparseq] def bindTaskToFuture[T](task: Task[T]): Future[T] = {
    // Create a Promise for extracting Future
    val scalaPromise: Promise[T] = Promise[T]()
    // Bind
    addTaskListener(task, scalaPromise.success, scalaPromise.failure)
    // Return the Future
    scalaPromise.future
  }

  /**
   * The method bindTaskToCompletionStage binds a `CompletionStage[T]` to a ParSeq `Task[T]`, for both success and
   * failure.
   *
   * @param task The ParSeq Task
   * @tparam T The type parameter of the ParSeq Task and the CompletionStage
   * @return The CompletionStage
   */
  private[playparseq] def bindTaskToCompletionStage[T](task: Task[T]): CompletionStage[T] = {
    // Create a CompletableFuture
    val completableFuture: CompletableFuture[T] = new CompletableFuture[T]
    // Bind
    addTaskListener(task, completableFuture.complete, completableFuture.completeExceptionally)
    // Return the CompletionStage
    completableFuture
  }

  /**
   * The method addTaskListener adds a `PromiseListener[T]` to a ParSeq `Task[T]` for binding success and failure to
   * the corresponding handlers.
   *
   * @param task The ParSeq Task
   * @param success The success handler
   * @param failure The failure handler
   * @tparam T The type parameter of the ParSeq Task
   */
  private def addTaskListener[T](task: Task[T], success: T => Any, failure: Throwable => Any): Unit = {
    task.addListener(new PromiseListener[T] {
      override def onResolved(parSeqPromise: ParSeqPromise[T]): Unit = {
        if (parSeqPromise.isFailed) failure(parSeqPromise.getError)
        else success(parSeqPromise.get)
      }
    })
  }

}
