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
import com.linkedin.playparseq.s.PlayParSeq
import com.linkedin.playparseq.s.PlayParSeqImplicits._
import javax.inject.Inject
import com.linkedin.playparseq.trace.s.ParSeqTraceAction
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Action, AnyContent, Controller}
import scala.concurrent.Future


/**
 * The class SingleTaskSample is a Controller to show how to use [[ParSeqTraceAction]] for generating ParSeq Trace of
 * one ParSeq Task.
 *
 * @param playParSeq The injected [[PlayParSeq]] component
 * @param parSeqTraceAction The injected [[ParSeqTraceAction]] component
 * @author Yinan Ding (yding@linkedin.com)
 */
class SingleTaskSample @Inject()(playParSeq: PlayParSeq, parSeqTraceAction: ParSeqTraceAction) extends Controller {

  /**
   * The method demo runs one Task, and is able to show the ParSeq Trace if the request has `parseq-trace=true`.
   * Note that, in general you shouldn't be running multiple ParSeq Tasks, otherwise the order of execution might not be
   * accurate, which minimizes the benefits of ParSeq.
   *
   * @return The Action
   */
  def demo: Action[AnyContent] = parSeqTraceAction.async(implicit request => {
    // Run the Task
    playParSeq.runTask(
      // In parallel
      Task.par(
        // Convert to ParSeq Task
        playParSeq.toTask("hello", () => Future("Hello".substring(0))),
        // Simple ParSeq Task
        Task.value("world", "World")
      ).map("concatenate", (h: String, w: String) => Ok(h + " " + w)))
  })
}
