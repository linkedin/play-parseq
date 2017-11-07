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
package controllers.s

import com.linkedin.parseq.Task
import com.linkedin.parseq.httpclient.HttpClient
import com.linkedin.playparseq.s.PlayParSeq
import com.linkedin.playparseq.s.PlayParSeqImplicits._
import com.linkedin.playparseq.trace.s.ParSeqTraceAction
import com.ning.http.client.Response
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}
import scala.concurrent.{ExecutionContext, Future}


/**
 * The class MultipleTasksSample is a Controller to show how to use [[ParSeqTraceAction]] for generating ParSeq Trace of
 * multiple ParSeq Tasks.
 *
 * @param ws The injected WSClient component
 * @param playParSeq The injected [[PlayParSeq]] component
 * @param parSeqTraceAction The injected [[ParSeqTraceAction]] component
 * @param executionContext The injected [[ExecutionContext]] component
 * @author Yinan Ding (yding@linkedin.com)
 */
class MultipleTasksSample @Inject()(ws: WSClient, playParSeq: PlayParSeq, parSeqTraceAction: ParSeqTraceAction)(implicit executionContext: ExecutionContext) extends Controller {

  /**
   * The method demo runs two independent Tasks, and is able to show the ParSeq Trace if the request has
   * `parseq-trace=true`.
   * Note that, in general you shouldn't be running multiple ParSeq Tasks, otherwise the order of execution might not be
   * accurate, which minimizes the benefits of ParSeq.
   *
   * @return The Action
   */
  def demo: Action[AnyContent] = parSeqTraceAction.async(implicit request => {
    // Run an independent Task
    playParSeq.runTask(getLengthTask("http://www.yahoo.com"))
    // Run another Task
    playParSeq.runTask(
      // In parallel
      Task.par(
        // Convert to ParSeq Task
        playParSeq.toTask("http://www.bing.com", () => getLengthFuture("http://www.bing.com")),
        // Complex ParSeq Task
        getLengthTask("http://www.google.com")
      ).map("sum", (g: Int, b: Int) => Ok((g + b).toString)))
  })

  /**
   * The method getLengthFuture creates a Future for getting the length of an HTTP request body.
   *
   * @param url The URL
   * @return The Future
   */
  private[this] def getLengthFuture(url: String): Future[Int] = ws.url(url).get().map(_.body.length)

  /**
   * The method getLengthTask creates a ParSeq Task for getting the length of an HTTP request body.
   *
   * @param url The URL
   * @return The ParSeq Task
   */
  private[this] def getLengthTask(url: String): Task[Int] = HttpClient.get(url).task().map(url, (r: Response) => r.getResponseBody.length)

}
