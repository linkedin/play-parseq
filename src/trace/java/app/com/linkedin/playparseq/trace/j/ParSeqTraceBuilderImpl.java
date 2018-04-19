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
package com.linkedin.playparseq.trace.j;

import akka.stream.Materializer;
import com.linkedin.parseq.Task;
import com.linkedin.playparseq.j.stores.ParSeqTaskStore;
import com.linkedin.playparseq.trace.j.renderers.ParSeqTraceRenderer;
import com.linkedin.playparseq.trace.j.sensors.ParSeqTraceSensor;
import com.linkedin.playparseq.trace.utils.PlayParSeqTraceHelper;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;


/**
 * The class ParSeqTraceBuilderImpl is an implementation of {@link ParSeqTraceBuilder} with the help from the class
 * {@link PlayParSeqTraceHelper}.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
public class ParSeqTraceBuilderImpl extends PlayParSeqTraceHelper implements ParSeqTraceBuilder {

  /**
   * The field _materializer is an Akka Materializer for consuming result body stream.
   */
  private final Materializer _materializer;

  /**
   * The field _httpExecutionContext is a {@link HttpExecutionContext} for setting Java async task's executor.
   */
  private final HttpExecutionContext _httpExecutionContext;

  /**
   * The constructor injects the Materializer.
   *
   * @param materializer The injected Materializer component
   * @param httpExecutionContext The injected HttpExecutionContext component
   */
  @Inject
  public ParSeqTraceBuilderImpl(final Materializer materializer, final HttpExecutionContext httpExecutionContext) {
    _materializer = materializer;
    _httpExecutionContext = httpExecutionContext;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public CompletionStage<Result> build(final Http.Context context, final CompletionStage<Result> origin,
                                       final ParSeqTaskStore parSeqTaskStore, final ParSeqTraceSensor parSeqTraceSensor,
                                       final ParSeqTraceRenderer parSeqTraceRenderer) {
    // Sense
    if (parSeqTraceSensor.isEnabled(context, parSeqTaskStore)) {
      // Bind independent Tasks
      Set<CompletionStage<Object>> completionStages = parSeqTaskStore.get(context).stream()
          .map(t -> bindTaskToCompletionStage((Task<Object>) t)).collect(Collectors.toSet());
      // Consume the origin
      completionStages.add(origin.thenComposeAsync(r -> consumeResult(r, _materializer)));
      // Combine all CompletionStages into one, which is completed when all CompletionStages complete, then render
      return CompletableFuture.allOf(
          completionStages.stream().map(CompletionStage::toCompletableFuture).toArray(CompletableFuture[]::new))
          .thenComposeAsync(__ -> parSeqTraceRenderer.render(context, parSeqTaskStore),
              _httpExecutionContext.current());
    } else {
      return origin;
    }
  }

}
