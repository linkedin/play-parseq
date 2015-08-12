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
import java.util.HashSet;
import org.junit.Test;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.mvc.Results.notFound;
import static play.mvc.Results.ok;
import static play.test.Helpers.*;


/**
 * The class ParSeqTraceBuilderImplTest is a test class for {@link ParSeqTraceBuilderImpl}.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public class ParSeqTraceBuilderImplTest {

  /**
   * The field DEFAULT_TIME_OUT is the default time out value for retrieving data from Play Promise in the unit of
   * milliseconds.
   */
  public final static int DEFAULT_TIME_OUT = 5000;

  /**
   * The method canBuildTrace tests the ability of building ParSeq Trace.
   */
  @Test
  public void canBuildTrace() {
    String render = "render";
    // Mock ParSeqTaskStore
    ParSeqTaskStore mockStore = mock(ParSeqTaskStore.class);
    when(mockStore.get()).thenReturn(new HashSet<>());
    // Mock ParSeqTraceSensor
    ParSeqTraceSensor mockTraceSensor = mock(ParSeqTraceSensor.class);
    when(mockTraceSensor.isEnabled(any(Http.Context.class), any(ParSeqTaskStore.class))).thenReturn(true);
    // Mock ParSeqTraceRenderer
    ParSeqTraceRenderer mockTraceRenderer = mock(ParSeqTraceRenderer.class);
    when(mockTraceRenderer.render(any(ParSeqTaskStore.class))).thenReturn(F.Promise.pure(ok(render)));
    // Build ParSeq Trace
    ParSeqTraceBuilderImpl playParSeqTraceImpl = new ParSeqTraceBuilderImpl();
    Result result =
        playParSeqTraceImpl.build(F.Promise.pure(notFound("origin")), mock(Http.Context.class), mockStore,
            mockTraceSensor, mockTraceRenderer).get(DEFAULT_TIME_OUT);
    // Assert the status and the content
    assertEquals(OK, result.status());
    assertEquals("text/plain", result.contentType());
    assertEquals(render, contentAsString(result));
  }

  /**
   * The method canShowOriginWhenTraceDisabled tests the ability of returning origin when the ParSeq Trace is disabled.
   */
  @Test
  public void canShowOriginWhenTraceDisabled() {
    String origin = "origin";
    // Mock ParSeqTaskStore
    ParSeqTaskStore mockStore = mock(ParSeqTaskStore.class);
    when(mockStore.get()).thenReturn(new HashSet<>());
    // Mock ParSeqTraceSensor
    ParSeqTraceSensor mockTraceSensor = mock(ParSeqTraceSensor.class);
    when(mockTraceSensor.isEnabled(any(Http.Context.class), any(ParSeqTaskStore.class))).thenReturn(false);
    // Mock ParSeqTraceRenderer
    ParSeqTraceRenderer mockTraceRenderer = mock(ParSeqTraceRenderer.class);
    when(mockTraceRenderer.render(any(ParSeqTaskStore.class))).thenReturn(F.Promise.pure(ok("render")));
    // Build ParSeq Trace
    ParSeqTraceBuilderImpl playParSeqTraceImpl = new ParSeqTraceBuilderImpl();
    Result result =
        playParSeqTraceImpl.build(F.Promise.pure(notFound(origin)), mock(Http.Context.class), mockStore,
            mockTraceSensor, mockTraceRenderer).get(DEFAULT_TIME_OUT);
    // Assert the status and the content
    assertEquals(NOT_FOUND, result.status());
    assertEquals("text/plain", result.contentType());
    assertEquals(origin, contentAsString(result));
  }
}
