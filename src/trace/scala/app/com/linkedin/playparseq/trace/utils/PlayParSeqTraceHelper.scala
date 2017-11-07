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
package com.linkedin.playparseq.trace.utils

import akka.Done
import akka.japi.function.Procedure
import akka.stream.Materializer
import akka.util.ByteString
import com.linkedin.playparseq.s.PlayParSeqImplicits._
import com.linkedin.playparseq.utils.PlayParSeqHelper
import java.util.concurrent.CompletionStage
import play.api.mvc.Result
import play.mvc.{Result => JavaResult}
import scala.concurrent.Future


/**
 * The class PlayParSeqTraceHelper provides consuming for `Result` together with the help from the class
 * [[PlayParSeqHelper]].
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
private[playparseq] abstract class PlayParSeqTraceHelper extends PlayParSeqHelper {

  /**
   * The method consumeResult consumes the `Result` body stream.
   *
   * @param result The Result
   * @param materializer The Materializer for consuming the data stream
   * @return The consumed Result
   */
  private[playparseq] def consumeResult(result: Result)(implicit materializer: Materializer): Future[Any] = result.body.dataStream.runForeach(_ => ())

  /**
   * The method consumeResult consumes the Java `Result` body stream.
   *
   * @param result The Result
   * @param materializer The Materializer for consuming the data stream
   * @return The consumed Result
   */
  private[playparseq] def consumeResult(result: JavaResult, materializer: Materializer): CompletionStage[Object] = result.body.dataStream.runForeach(
    new Procedure[ByteString] {
      @scala.throws[Exception](classOf[Exception])
      override def apply(param: ByteString): Unit = ()
    }, materializer).thenApplyAsync((done: Done) => done.asInstanceOf[Object])

}
