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

import com.linkedin.parseq.Task;
import com.linkedin.playparseq.j.PlayParSeq;
import com.linkedin.playparseq.trace.j.ParSeqTraceAction;
import javax.inject.Inject;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;


/**
 * The class SingleTaskSample is a Controller to show how to use {@link ParSeqTraceAction} for generating ParSeq Trace
 * of one ParSeq Task.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public class SingleTaskSample extends Controller {

  /**
   * The field _playParSeq is a {@link PlayParSeq} for conversion and execution of ParSeq Tasks.
   */
  private final PlayParSeq _playParSeq;

  /**
   * The constructor injects the {@link PlayParSeq}.
   *
   * @param playParSeq The injected {@link PlayParSeq} component
   */
  @Inject
  public SingleTaskSample(final PlayParSeq playParSeq) {
    this._playParSeq = playParSeq;
  }

  /**
   * The method demo runs one Task, and is able to show the ParSeq Trace if the request has `parseq-trace=true`.
   * Note that, in general you shouldn't be running multiple ParSeq Tasks, otherwise the order of execution is not
   * accurate, which minimizes the benefits of ParSeq.
   *
   * @return The Promise of Action Result
   */
  @With(ParSeqTraceAction.class)
  public F.Promise<Result> demo() {
    // Run the Task
    return _playParSeq.runTask(Http.Context.current(),
        // In parallel
        Task.par(
            // Convert to ParSeq Task
            _playParSeq.toTask("hello", () -> F.Promise.promise(() -> "Hello")),
            // Simple ParSeq Task
            Task.value("world", "World")).map((h, w) -> ok(h + " " + w)));
  }
}
