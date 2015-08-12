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
package com.linkedin.playparseq.trace.s

import com.linkedin.playparseq.s.stores.ParSeqTaskStore
import com.linkedin.playparseq.trace.s.sensors.ParSeqTraceSensor
import com.linkedin.playparseq.trace.s.renderers.ParSeqTraceRenderer
import org.specs2.mock.Mockito
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.test.PlaySpecification
import scala.collection.mutable
import scala.concurrent.Future


/**
 * The class ParSeqTraceBuilderImplSpec is a specification class for [[ParSeqTraceBuilderImpl]].
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
class ParSeqTraceBuilderImplSpec extends PlaySpecification with Mockito {

  "The ParSeqTraceBuilderImpl" should {
    "be able to build ParSeq Trace" in {
      val render: String = "render"
      // Mock ParSeqTaskStore
      val mockStore: ParSeqTaskStore = mock[ParSeqTaskStore]
      mockStore.get(any) returns mutable.Set.empty
      // Mock ParSeqTraceSensor
      val mockTraceSensor: ParSeqTraceSensor = mock[ParSeqTraceSensor]
      mockTraceSensor.isEnabled(any, any) returns true
      // Mock ParSeqTraceRenderer
      val mockTraceRenderer: ParSeqTraceRenderer = mock[ParSeqTraceRenderer]
      mockTraceRenderer.render(any, any) returns Future.successful(Results.Ok(render))
      // Build ParSeq Trace
      val playParSeqTraceImpl: ParSeqTraceBuilderImpl = new ParSeqTraceBuilderImpl
      val result: Future[Result] = playParSeqTraceImpl.build(Future.successful(Results.NotFound("origin")), mock[RequestHeader], mockStore, mockTraceSensor, mockTraceRenderer)
      // Assert the status and the content
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      contentAsString(result) must equalTo(render)
    }
  }

  "The PlayParSeqTrace" should {
    "be able to return origin when ParSeq Trace is disabled" in {
      val origin: String = "origin"
      // Mock ParSeqTaskStore
      val mockStore: ParSeqTaskStore = mock[ParSeqTaskStore]
      mockStore.get(any) returns mutable.Set.empty
      // Mock ParSeqTraceSensor
      val mockTraceSensor: ParSeqTraceSensor = mock[ParSeqTraceSensor]
      mockTraceSensor.isEnabled(any, any) returns false
      // Mock ParSeqTraceRenderer
      val mockTraceRenderer: ParSeqTraceRenderer = mock[ParSeqTraceRenderer]
      mockTraceRenderer.render(any, any) returns Future.successful(Results.Ok("render"))
      // Build ParSeq Trace
      val playParSeqTraceImpl: ParSeqTraceBuilderImpl = new ParSeqTraceBuilderImpl
      val result: Future[Result] = playParSeqTraceImpl.build(Future.successful(Results.NotFound("origin")), mock[RequestHeader], mockStore, mockTraceSensor, mockTraceRenderer)
      // Assert the status and the content
      status(result) must equalTo(NOT_FOUND)
      contentType(result) must beSome("text/plain")
      contentAsString(result) must equalTo(origin)
    }
  }
}
