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

import com.linkedin.playparseq.j.stores.ParSeqTaskStore;
import com.linkedin.playparseq.trace.j.renderers.ParSeqTraceRenderer;
import com.linkedin.playparseq.trace.j.sensors.ParSeqTraceSensor;
import javax.inject.Inject;
import play.libs.F;
import play.mvc.Action.Simple;
import play.mvc.Http;
import play.mvc.Result;


/**
 * The class ParSeqTraceAction is an Action which composes with {@link ParSeqTraceBuilder} to hand origin Result off in
 * order to determine whether to show ParSeq Trace data or the origin Result, if so generate the ParSeq Trace Result.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public class ParSeqTraceAction extends Simple {

  /**
   * The field _parSeqTaskStore is a {@link ParSeqTaskStore} for getting ParSeq Tasks.
   */
  private final ParSeqTaskStore _parSeqTaskStore;

  /**
   * The field _parSeqTraceBuilder is a {@link ParSeqTraceBuilder} for building ParSeq Trace.
   */
  private final ParSeqTraceBuilder _parSeqTraceBuilder;

  /**
   * The field _parSeqTraceSensor is a {@link ParSeqTraceSensor} for deciding whether ParSeq Trace is enabled or not.
   */
  private final ParSeqTraceSensor _parSeqTraceSensor;

  /**
   * The field _parSeqTraceRenderer is a {@link ParSeqTraceRenderer} for generating ParSeq Trace.
   */
  private final ParSeqTraceRenderer _parSeqTraceRenderer;

  /**
   * The constructor injects the {@link ParSeqTaskStore}, the {@link ParSeqTraceBuilder}, the {@link ParSeqTraceSensor}
   * and the {@link ParSeqTraceRenderer}.
   *
   * @param parSeqTaskStore The injected {@link ParSeqTaskStore} component
   * @param parSeqTraceBuilder The injected {@link ParSeqTraceBuilder} component
   * @param parSeqTraceSensor The injected {@link ParSeqTraceSensor} component
   * @param parSeqTraceRenderer The injected {@link ParSeqTraceRenderer} component
   */
  @Inject
  public ParSeqTraceAction(final ParSeqTaskStore parSeqTaskStore, final ParSeqTraceBuilder parSeqTraceBuilder,
      final ParSeqTraceSensor parSeqTraceSensor, final ParSeqTraceRenderer parSeqTraceRenderer) {
    super();
    this._parSeqTaskStore = parSeqTaskStore;
    this._parSeqTraceBuilder = parSeqTraceBuilder;
    this._parSeqTraceSensor = parSeqTraceSensor;
    this._parSeqTraceRenderer = parSeqTraceRenderer;
  }

  /**
   * The method call composes with {@link ParSeqTraceBuilder} to build ParSeq Trace for the request..
   *
   * @param context The HTTP Context
   * @return The Play Promise of Result
   * @throws Throwable The exception from show
   */
  @Override
  public F.Promise<Result> call(final Http.Context context)
      throws Throwable {
    return _parSeqTraceBuilder
        .build(context, delegate.call(context), _parSeqTaskStore, _parSeqTraceSensor, _parSeqTraceRenderer);
  }
}
