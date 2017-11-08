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

import com.linkedin.parseq.Task;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;
import play.mvc.Http;


/**
 * The interface PlayParSeq defines the conversions from a {@code Callable<CompletionStage<T>>} to a ParSeq
 * {@code Task<T>}, and also the execution of a ParSeq {@code Task<T>} which returns a {@code CompletionStage<T>}, in
 * the mean time putting Tasks into store.
 * Note that, in general you shouldn't be running multiple ParSeq Tasks, otherwise the order of execution may not be
 * accurate, which minimizes the benefits of ParSeq.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public interface PlayParSeq {

  /**
   * The method toTask converts a {@code Callable<CompletionStage<T>>} to a ParSeq {@code Task<T>}.
   *
   * @param name The String which describes the Task and shows up in a trace
   * @param f The Callable which returns a CompletionStage
   * @param <T> The type parameter of the CompletionStage and the ParSeq Task
   * @return The ParSeq Task
   */
  <T> Task<T> toTask(final String name, final Callable<CompletionStage<T>> f);

  /**
   * The method toTask converts a {@code Callable<CompletionStage<T>>} to a ParSeq {@code Task<T>}, which binds with a
   * default name.
   *
   * @param f The Callable which returns a CompletionStage
   * @param <T> The type parameter of the CompletionStage and the ParSeq Task
   * @return The ParSeq Task
   */
  <T> Task<T> toTask(final Callable<CompletionStage<T>> f);

  /**
   * The method runTask executes a ParSeq {@code Task<T>} then generates a {@code CompletionStage<T>}, and puts into the
   * store.
   *
   * @param context The HTTP Context
   * @param task The ParSeq Task
   * @param <T> The type parameter of the ParSeq Task and the CompletionStage
   * @return The CompletionStage
   */
  <T> CompletionStage<T> runTask(final Http.Context context, final Task<T> task);

}
