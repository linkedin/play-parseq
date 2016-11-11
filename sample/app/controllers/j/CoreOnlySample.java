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
package controllers.j;

import com.linkedin.playparseq.j.PlayParSeq;
import javax.inject.Inject;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.html.input;


/**
 * The class CoreOnlySample is a Controller to show how to use {@link PlayParSeq} for the conversions to ParSeq Task and
 * the execution of ParSeq Task by substring jobs without enabling the ParSeq Trace feature.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public class CoreOnlySample extends Controller {

  /**
   * The field _playParSeq is the injected {@link PlayParSeq} component.
   */
  private final PlayParSeq _playParSeq;

  /**
   * The field DEFAULT_FAILURE is the default failure output.
   */
  public final static String DEFAULT_FAILURE = "Start Index Error";

  /**
   * The constructor sets the injected {@link PlayParSeq} component.
   *
   * @param playParSeq The {@link PlayParSeq}
   */
  @Inject
  public CoreOnlySample(final PlayParSeq playParSeq) {
    this._playParSeq = playParSeq;
  }

  /**
   * The method input returns the input page.
   *
   * @return The Action Result
   */
  public Result input() {
    return ok(input.render());
  }

  /**
   * The method demo creates one ParSeq Task by converting from an F.Promise of substring job, then runs it to get a
   * Promise of Action Result with substring. The method only uses the core feature without enabling the ParSeq Trace
   * feature.
   * Note that, in general you shouldn't be running multiple ParSeq Tasks, otherwise the order of execution might not be
   * accurate, which minimizes the benefits of ParSeq.
   *
   * @param text The input String
   * @param start The substring start index
   * @return The Promise of Action Result
   */
  public F.Promise<Result> demo(final String text, final int start) {
    // Run the Task
    return _playParSeq.runTask(Http.Context.current(),
        // Convert to ParSeq Task
        _playParSeq.toTask("substring", () -> F.Promise.promise(() -> text.substring(start))
            // Recover
            .recover(t -> DEFAULT_FAILURE)).map("getResult", Results::ok));
  }
}
