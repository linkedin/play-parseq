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

import com.linkedin.playparseq.s.stores.{ContextRequest, ParSeqTaskStore}
import com.linkedin.playparseq.trace.s.renderers.ParSeqTraceRenderer
import com.linkedin.playparseq.trace.s.sensors.ParSeqTraceSensor
import com.linkedin.playparseq.trace.utils.ParSeqTraceHelper
import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import scala.collection.concurrent.TrieMap
import scala.concurrent.Future


/**
 * The trait ParSeqTraceBuilder defines building a ParSeq Trace Result using the [[ParSeqTraceRenderer]] if the
 * [[ParSeqTraceSensor]] determines ParSeq Trace is enabled.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
trait ParSeqTraceBuilder {

  /**
   * The method build builds the ParSeq Trace through the decision of [[ParSeqTraceSensor]] and the output of
   * [[ParSeqTraceRenderer]].
   *
   * @param origin The origin Future of Result
   * @param parSeqTaskStore The [[ParSeqTaskStore]] for getting ParSeq Tasks
   * @param parSeqTraceSensor The [[ParSeqTraceSensor]] for deciding whether ParSeq Trace is enabled or not
   * @param parSeqTraceRenderer The [[ParSeqTraceRenderer]] for generating the ParSeq Trace page
   * @param requestHeader The request
   * @return The Future of Result
   */
  def build(origin: Future[Result], parSeqTaskStore: ParSeqTaskStore, parSeqTraceSensor: ParSeqTraceSensor, parSeqTraceRenderer: ParSeqTraceRenderer)(implicit requestHeader: RequestHeader): Future[Result]
}

/**
 * The class ParSeqTraceBuilderImpl is an implementation of [[ParSeqTraceBuilder]] with the help from the class
 * [[ParSeqTraceHelper]].
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
class ParSeqTraceBuilderImpl extends ParSeqTraceHelper with ParSeqTraceBuilder {

  override def build(origin: Future[Result], parSeqTaskStore: ParSeqTaskStore, parSeqTraceSensor: ParSeqTraceSensor, parSeqTraceRenderer: ParSeqTraceRenderer)(implicit requestHeader: RequestHeader): Future[Result] = {
    // Sense
    if (parSeqTraceSensor.isEnabled(parSeqTaskStore)) {
      // Consume the origin and bind independent Tasks
      val futures: Set[Future[Any]] = Set(origin.flatMap(r => consumeResult(r))) ++ parSeqTaskStore.get.map(bindTaskToFuture(_))
      // Render
      Future.sequence(futures).flatMap(list => parSeqTraceRenderer.render(parSeqTaskStore))
    } else origin
  }
}

/**
 * The class ParSeqTraceAction is an ActionFunction which transforms a normal Request to a [[ContextRequest]] in order
 * to put ParSeq Task into store for retrieving all Tasks within the scope of one request when building ParSeq Trace.
 * And it also composes with [[ParSeqTraceBuilder]] to hand origin Result off in order to determine whether to show
 * ParSeq Trace data or the origin Result, if so generate the ParSeq Trace Result.
 *
 * @param parSeqTaskStore The injected [[ParSeqTaskStore]] component
 * @param parSeqTraceBuilder The injected [[ParSeqTraceBuilder]] component
 * @param parSeqTraceSensor The injected [[ParSeqTraceSensor]] component
 * @param parSeqTraceRenderer The injected [[ParSeqTraceRenderer]] component
 * @author Yinan Ding (yding@linkedin.com)
 */
class ParSeqTraceAction @Inject()(parSeqTaskStore: ParSeqTaskStore, parSeqTraceBuilder: ParSeqTraceBuilder, parSeqTraceSensor: ParSeqTraceSensor, parSeqTraceRenderer: ParSeqTraceRenderer) extends ActionBuilder[ContextRequest] with ActionFunction[Request, ContextRequest] {

  /**
   * The method invokeBlock transforms a normal Request to a [[ContextRequest]] and composes with
   * [[ParSeqTraceBuilder]] to build ParSeq Trace for the Request.
   *
   * @param request The origin Request
   * @param block The block of origin Request process
   * @tparam A The type parameter of the Request
   * @return The Future of Result
   */
  override def invokeBlock[A](request: Request[A], block: (ContextRequest[A]) => Future[Result]): Future[Result] = {
    // Transform
    implicit val contextRequest = new ContextRequest[A](TrieMap.empty[String, Any], request)
    // Compose
    parSeqTraceBuilder.build(block(contextRequest), parSeqTaskStore, parSeqTraceSensor, parSeqTraceRenderer)
  }
}
