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
package com.linkedin.playparseq.j;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.playparseq.j.stores.ParSeqTaskStore;
import com.linkedin.playparseq.utils.PlayParSeqHelper;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;


/**
 * The class PlayParSeqImpl is an implementation of the interface {@link PlayParSeq} with the help from the class
 * {@link PlayParSeqHelper}.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
public class PlayParSeqImpl extends PlayParSeqHelper implements PlayParSeq {

  /**
   * The field DEFAULT_TASK_NAME is the default name of ParSeq Task.
   */
  public final static String DEFAULT_TASK_NAME = "fromPlayCompletionStage";

  /**
   * The field _engine is a ParSeq Engine for running ParSeq Task.
   */
  private final Engine _engine;

  /**
   * The field _parSeqTaskStore is a {@link ParSeqTaskStore} for storing ParSeq Tasks.
   */
  private final ParSeqTaskStore _parSeqTaskStore;

  /**
   * The constructor injects the ParSeq Engine and the {@link ParSeqTaskStore}.
   *
   * @param engine The injected ParSeq Engine component
   * @param parSeqTaskStore The injected {@link ParSeqTaskStore} component
   */
  @Inject
  public PlayParSeqImpl(final Engine engine, final ParSeqTaskStore parSeqTaskStore) {
    _engine = engine;
    _parSeqTaskStore = parSeqTaskStore;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> Task<T> toTask(final String name, final Callable<CompletionStage<T>> f) {
    return toTask(name, f, null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> Task<T> toTask(final String name, final Callable<CompletionStage<T>> f, final HttpExecutionContext executionContext) {
    // Bind a Task to the CompletionStage for both success and failure
    return Task.async(name, transferCompletionStageCallableToPromiseCallable(f, executionContext));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> Task<T> toTask(final Callable<CompletionStage<T>> f) {
    return toTask(DEFAULT_TASK_NAME, f);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> Task<T> toTask(final Callable<CompletionStage<T>> f, final HttpExecutionContext executionContext) {
    return toTask(DEFAULT_TASK_NAME, f, executionContext);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> CompletionStage<T> runTask(final Http.Context context, final Task<T> task) {
    // Bind a CompletionStage to the ParSeq Task
    CompletionStage<T> completionStage = bindTaskToCompletionStage(task);
    // Put the ParSeq Task into store
    _parSeqTaskStore.put(context, task);
    // Run the ParSeq Task
    _engine.run(task);
    // Return the CompletionStage
    return completionStage;
  }

  private <T> Callable<Promise<? extends T>> transferCompletionStageCallableToPromiseCallable(
      final Callable<CompletionStage<T>> fn,
      final HttpExecutionContext executionContext) {

    return () -> {
      final SettablePromise<T> promise = Promises.settable();
      final CompletionStage<T> future = fn.call();
      if (null == executionContext) {
        future.whenCompleteAsync(transferCompletionStageToPromise(promise));
      } else {
        future.whenCompleteAsync(transferCompletionStageToPromise(promise), executionContext.current());
      }
      return promise;
    };
  }

  private <T> BiConsumer<? super T, ? super Throwable> transferCompletionStageToPromise(final SettablePromise<T> promise) {
    return (result, exception) -> {
      if (exception != null) {
        promise.fail(exception);
      } else {
        promise.done(result);
      }
    };
  }

}
