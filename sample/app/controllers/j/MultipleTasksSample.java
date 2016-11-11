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
import com.linkedin.parseq.httpclient.HttpClient;
import com.linkedin.playparseq.j.PlayParSeq;
import com.linkedin.playparseq.trace.j.ParSeqTraceAction;
import javax.inject.Inject;
import play.libs.F;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;


/**
 * The class MultipleTasksSample is a Controller to show how to use {@link ParSeqTraceAction} for generating ParSeq
 * Trace of multiple ParSeq Tasks.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public class MultipleTasksSample extends Controller {

  /**
   * The field _ws is the WSClient for making HTTP calls.
   */
  private final WSClient _ws;

  /**
   * The field _playParSeq is a {@link PlayParSeq} for conversion and execution of ParSeq Tasks.
   */
  private final PlayParSeq _playParSeq;

  /**
   * The constructor injects the WSClient and the {@link PlayParSeq}.
   *
   * @param ws The injected WSClient component
   * @param playParSeq The injected {@link PlayParSeq} component
   */
  @Inject
  public MultipleTasksSample(final WSClient ws, final PlayParSeq playParSeq) {
    this._ws = ws;
    this._playParSeq = playParSeq;
  }

  /**
   * The method demo runs two independent Tasks, and is able to show the ParSeq Trace if the request has
   * `parseq-trace=true`.
   * Note that, in general you shouldn't be running multiple ParSeq Tasks, otherwise the order of execution might not be
   * accurate, which minimizes the benefits of ParSeq.
   *
   * @return The Promise of Action Result
   */
  @With(ParSeqTraceAction.class)
  public F.Promise<Result> demo() {
    Http.Context context = Http.Context.current();
    // Run an independent Task
    _playParSeq.runTask(context, getLengthTask("http://www.yahoo.com"));
    // Run another Task
    return _playParSeq.runTask(context,
        // In parallel
        Task.par(
            // Convert to ParSeq Task
            _playParSeq.toTask("http://www.bing.com", () -> getLengthPromise("http://www.bing.com")),
            // Complex ParSeq Task
            getLengthTask("http://www.google.com")
        ).map((g, b) -> ok(String.valueOf(g + b))));
  }

  /**
   * The method getLengthPromise creates an F.Promise for getting the length of an HTTP request body.
   *
   * @param url The URL
   * @return The Play Promise
   */
  private F.Promise<Integer> getLengthPromise(final String url) {
    return _ws.url(url).get().map(r -> r.getBody().length());
  }

  /**
   * The method getLengthTask creates a ParSeq Task for getting the length of an HTTP request body.
   *
   * @param url The URL
   * @return The ParSeq Task
   */
  private Task<Integer> getLengthTask(final String url) {
    return HttpClient.get(url).task().map(url, r -> r.getResponseBody().length());
  }
}
