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
import java.util.concurrent.CompletionStage;
import play.mvc.Http;
import play.mvc.Result;


/**
 * The interface ParSeqTraceBuilder defines building a ParSeq Trace Result using the {@link ParSeqTraceRenderer} if the
 * {@link ParSeqTraceSensor} determines ParSeq Trace is enabled.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public interface ParSeqTraceBuilder {

  /**
   * The method show returns the trace Result.
   *
   * @param context The HTTP Context
   * @param origin The origin CompletionStage of Result
   * @param parSeqTaskStore The {@link ParSeqTaskStore} for getting ParSeq Tasks
   * @param parSeqTraceSensor The {@link ParSeqTraceSensor} for deciding whether ParSeq Trace is enabled or not
   * @param parSeqTraceRenderer The {@link ParSeqTraceRenderer} for generating the ParSeq Trace page
   * @return The CompletionStage of Result
   */
  CompletionStage<Result> build(final Http.Context context, final CompletionStage<Result> origin,
      final ParSeqTaskStore parSeqTaskStore, final ParSeqTraceSensor parSeqTraceSensor,
      final ParSeqTraceRenderer parSeqTraceRenderer);

}
