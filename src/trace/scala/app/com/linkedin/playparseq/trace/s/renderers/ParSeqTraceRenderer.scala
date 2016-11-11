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
package com.linkedin.playparseq.trace.s.renderers

import com.linkedin.parseq.trace.{ShallowTrace, Trace, TraceRelationship}
import com.linkedin.playparseq.s.stores.ParSeqTaskStore
import com.linkedin.playparseq.trace.utils.ParSeqTraceBaseVisualizer
import javax.inject.{Inject, Singleton}
import play.api.Environment
import play.api.http.HttpConfiguration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{RequestHeader, Result, Results}
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.Future


/**
 * The trait ParSeqTraceRenderer defines rendering a `Future[Result]` of ParSeq Trace from request and
 * [[ParSeqTaskStore]].
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
trait ParSeqTraceRenderer {

  /**
   * The method render generates a `Future[Result]` of ParSeq Trace from request and [[ParSeqTaskStore]].
   *
   * @param parSeqTaskStore The [[ParSeqTaskStore]] for getting ParSeq Tasks
   * @param requestHeader The request
   * @return The Future of Result
   */
  def render(parSeqTaskStore: ParSeqTaskStore)(implicit requestHeader: RequestHeader): Future[Result]
}

/**
 * The class ParSeqTraceRendererImpl is an implementation of the trait [[ParSeqTraceRenderer]] with the help from the
 * class [[ParSeqTraceBaseVisualizer]].
 *
 * @param environment The injected Environment component
 * @param httpConfiguration The injected HttpConfiguration component
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
class ParSeqTraceRendererImpl @Inject()(environment: Environment, httpConfiguration: HttpConfiguration) extends ParSeqTraceBaseVisualizer with ParSeqTraceRenderer {

  override def render(parSeqTaskStore: ParSeqTaskStore)(implicit requestHeader: RequestHeader): Future[Result] =
    Future {
      val traces: mutable.Set[Trace] = parSeqTaskStore.get.map(_.getTrace)
      val traceMap: Map[java.lang.Long, ShallowTrace] = traces.foldLeft(Map[java.lang.Long, ShallowTrace]())(_ ++ _.getTraceMap.asScala)
      val relationships: Set[TraceRelationship] = traces.foldLeft(Set[TraceRelationship]())(_ ++ _.getRelationships.asScala)
      // Generate Result of ParSeq Trace
      Option(showTrace(new Trace(traceMap.asJava, relationships.asJava), environment, httpConfiguration)).map(Results.Ok(_).as("text/html"))
        .getOrElse(Results.InternalServerError("Can't show Trace."))
    }
}
