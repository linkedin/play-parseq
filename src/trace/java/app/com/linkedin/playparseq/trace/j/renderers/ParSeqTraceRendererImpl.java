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
package com.linkedin.playparseq.trace.j.renderers;

import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.trace.TraceRelationship;
import com.linkedin.playparseq.j.stores.ParSeqTaskStore;
import com.linkedin.playparseq.trace.utils.ParSeqTraceBaseVisualizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import play.api.http.HttpConfiguration;
import play.Application;
import play.libs.F;
import play.mvc.Result;
import play.mvc.Results;


/**
 * The class ParSeqTraceRendererImpl is an implementation of the interface {@link ParSeqTraceRenderer} with the
 * help from the class {@link ParSeqTraceBaseVisualizer}.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
public class ParSeqTraceRendererImpl extends ParSeqTraceBaseVisualizer implements ParSeqTraceRenderer {

  /**
   * The field _application is the injected Application.
   */
  private final Application _application;

  /**
   * The field _httpConfiguration is the injected HttpConfiguration.
   */
  private final HttpConfiguration _httpConfiguration;

  /**
   * The constructor injects the Application and the HttpConfiguration.
   *
   * @param application The injected Application component
   * @param httpConfiguration The injected HttpConfiguration component
   */
  @Inject
  public ParSeqTraceRendererImpl(final Application application, final HttpConfiguration httpConfiguration) {
    this._application = application;
    this._httpConfiguration = httpConfiguration;
  }

  @Override
  public F.Promise<Result> render(final ParSeqTaskStore parSeqTaskStore) {
    return F.Promise.promise(() -> {
      Map<Long, ShallowTrace> traceMap = new HashMap<>();
      Set<TraceRelationship> relationships = new HashSet<>();
      // Get all Tasks from the request out of the store and combine all Trace information
      parSeqTaskStore.get().forEach(task -> {
        Trace trace = task.getTrace();
        traceMap.putAll(trace.getTraceMap());
        relationships.addAll(trace.getRelationships());
      });
      // Generate Result of ParSeq Trace
      return Optional.ofNullable(showTrace(new Trace(traceMap, relationships), _application.getWrappedApplication(), _httpConfiguration))
          .map(s -> Results.ok(s).as("text/html")).orElse(Results.internalServerError("Can't show Trace."));
    });
  }
}
