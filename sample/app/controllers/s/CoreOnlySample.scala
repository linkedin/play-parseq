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

import com.linkedin.playparseq.s.PlayParSeq
import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Action, Controller}
import scala.concurrent.Future


/**
 * The class CoreOnlySample is a Controller to show how to use [[PlayParSeq]] for the conversions to ParSeq Task and the
 * execution of ParSeq Task by substring jobs without enabling the ParSeq Trace feature.
 *
 * @param playParSeq The injected [[PlayParSeq]] component
 * @author Yinan Ding (yding@linkedin.com)
 */
class CoreOnlySample @Inject()(playParSeq: PlayParSeq) extends Controller {

  /**
   * The field DefaultFailure is the default failure output.
   */
  val DefaultFailure = "Start Index Error"

  /**
   * The method input returns the Action for input page.
   *
   * @return The Action
   */
  def input() = Action {
    Ok(views.html.input())
  }

  /**
   * The method demo creates one ParSeq Task by converting from a Future of substring job, then runs it to get a Future
   * of Action with substring. The method only uses the core feature without enabling the ParSeq Trace feature.
   * Note that, in general you shouldn't be running multiple ParSeq Tasks, otherwise the order of execution might not be
   * accurate, which minimizes the benefits of ParSeq.
   *
   * @param text The input String
   * @param start The substring start index
   * @return The Future of Action
   */
  def demo(text: String, start: Int) = Action.async(implicit request => {
    // Run the Task
    playParSeq.runTask(
      // Convert to ParSeq Task
      playParSeq.toTask(
        "substring",
        () => Future(text.substring(start))
          // Recover
          .recover { case _ => DefaultFailure }
          .map(Ok(_))))
  })
}
