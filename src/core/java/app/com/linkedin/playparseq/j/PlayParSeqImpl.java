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
import com.linkedin.playparseq.j.stores.ParSeqTaskStore;
import com.linkedin.playparseq.utils.PlayParSeqHelper;
import java.util.concurrent.Callable;
import javax.inject.Inject;
import javax.inject.Singleton;

import play.core.j.FPromiseHelper;
import play.libs.F;
import play.mvc.Http;
import scala.concurrent.Future;
import scala.concurrent.Future$;
import scala.concurrent.Promise;
import scala.runtime.AbstractFunction0;


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
  public final static String DEFAULT_TASK_NAME = "fromPlayPromise";

  /**
   * The field _engine is a ParSeq Engine for running ParSeq Task.
   */
  private final Engine _engine;

  /**
   * The field _parSeqTaskStore is a {@link ParSeqTaskStore} for storing ParSeq Tasks.
   */
  private final ParSeqTaskStore _parSeqTaskStore;

  /**
   * The constructor injects the ParSeq Engine, the {@link ParSeqTaskStore} and the {@link PlayParSeqHelper}.
   *
   * @param engine The injected ParSeq Engine component
   * @param parSeqTaskStore The injected {@link ParSeqTaskStore} component
   */
  @Inject
  public PlayParSeqImpl(final Engine engine, final ParSeqTaskStore parSeqTaskStore) {
    _engine = engine;
    _parSeqTaskStore = parSeqTaskStore;
  }

  @Override
  public <T> Task<T> toTask(final String name, final Callable<F.Promise<T>> f) {
    // Bind a Task to a Scala Future from the Play Promise
    return bindFutureToTask(name, new AbstractFunction0<Future<T>>() {
      @Override
      public Future<T> apply() {
        try {
          return f.call().wrapped();
        } catch (Exception e) {
          return Future$.MODULE$.failed(e);
        }
      }
    });
  }

  @Override
  public <T> Task<T> toTask(final Callable<F.Promise<T>> f) {
    return toTask(DEFAULT_TASK_NAME, f);
  }

  @Override
  public <T> F.Promise<T> runTask(final Http.Context context, final Task<T> task) {
    Promise<T> scalaPromise = FPromiseHelper.empty();
    // Wrap a Scala Future which binds to the ParSeq Task
    Task<T> wrappedTask = bindTaskToPromise(task, scalaPromise);
    // Put the ParSeq Task into store
    _parSeqTaskStore.put(context, wrappedTask);
    // Run the ParSeq Task
    _engine.run(wrappedTask);
    // Return the Play Promise
    return F.Promise.wrap(scalaPromise.future());
  }

  @Override
  public <T> F.Promise<T> runTask(final Task<T> task) {
    return runTask(Http.Context.current(), task);
  }
}
