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

import com.linkedin.parseq.Task;
import com.linkedin.playparseq.j.stores.ParSeqTaskStore;
import com.linkedin.playparseq.trace.j.renderers.ParSeqTraceRenderer;
import com.linkedin.playparseq.trace.j.sensors.ParSeqTraceSensor;
import com.linkedin.playparseq.trace.utils.ParSeqTraceHelper;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;


/**
 * The class ParSeqTraceBuilderImpl is an implementation of {@link ParSeqTraceBuilder} with the help from the class
 * {@link ParSeqTraceHelper}.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
public class ParSeqTraceBuilderImpl extends ParSeqTraceHelper implements ParSeqTraceBuilder {

  @SuppressWarnings("unchecked")
  @Override
  public F.Promise<Result> build(final F.Promise<Result> origin, final Http.Context context,
      final ParSeqTaskStore parSeqTaskStore, final ParSeqTraceSensor parSeqTraceSensor,
      final ParSeqTraceRenderer parSeqTraceRenderer) {
    // Sense
    if (parSeqTraceSensor.isEnabled(context, parSeqTaskStore)) {
      // Bind independent Tasks
      Set<F.Promise<Object>> promises =
          parSeqTaskStore.get().stream().map(t -> F.Promise.wrap(bindTaskToFuture((Task<Object>) t)))
              .collect(Collectors.toSet());
      // Consume the origin
      promises.add(origin.flatMap(r -> F.Promise.wrap(consumeResult(r.toScala()))));
      // Render
      return F.Promise.sequence(promises).flatMap(list -> parSeqTraceRenderer.render(parSeqTaskStore));
    } else {
      return origin;
    }
  }
}
