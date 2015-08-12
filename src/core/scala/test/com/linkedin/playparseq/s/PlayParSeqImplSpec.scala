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
package com.linkedin.playparseq.s

import com.linkedin.parseq.{Engine, EngineBuilder, Task}
import com.linkedin.playparseq.s.PlayParSeqImplicits._
import com.linkedin.playparseq.s.stores.ParSeqTaskStore
import java.util.concurrent.{Executors, ExecutorService, ScheduledExecutorService, TimeUnit}
import org.specs2.mock.Mockito
import org.specs2.specification.BeforeAfterEach
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.RequestHeader
import play.api.test.PlaySpecification
import scala.concurrent.Future


/**
 * The class PlayParSeqImplSpec is a specification class for [[PlayParSeqImpl]].
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
class PlayParSeqImplSpec extends PlaySpecification with BeforeAfterEach with Mockito {

  /**
   * The field playParSeqImpl is the [[PlayParSeqImpl]] to be tested.
   */
  private[this] var playParSeqImpl: PlayParSeqImpl = null

  /**
   * The field engine is a ParSeq Engine for running ParSeq Task.
   */
  private[this] var engine: Engine = null

  /**
   * The field taskScheduler is a task scheduler for ParSeq Engine.
   */
  private[this] var taskScheduler: ExecutorService = null

  /**
   * The field timerScheduler is a timer scheduler for ParSeq Engine.
   */
  private[this] var timerScheduler: ScheduledExecutorService = null

  /**
   * The field requestHeader is a mock RequestHeader for running Tasks.
   */
  private[this] implicit val requestHeader: RequestHeader = mock[RequestHeader]

  /**
   * The method before sets the ParSeq Engine and the [[PlayParSeqImpl]].
   *
   * @see <a href="https://github.com/linkedin/parseq/wiki/User's-Guide#creating-an-engine">ParSeq Wiki</a>
   */
  def before = {
    taskScheduler = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors + 1)
    timerScheduler = Executors.newSingleThreadScheduledExecutor
    engine = new EngineBuilder().setTaskExecutor(taskScheduler).setTimerScheduler(timerScheduler).build
    playParSeqImpl = new PlayParSeqImpl(engine, mock[ParSeqTaskStore])
  }

  /**
   * The method after tears the ParSeq Engine.
   *
   * @see <a href="https://github.com/linkedin/parseq/wiki/User's-Guide#creating-an-engine">ParSeq Wiki</a>
   */
  def after = {
    engine.shutdown()
    engine.awaitTermination(1, TimeUnit.SECONDS)
    taskScheduler.shutdown()
    timerScheduler.shutdown()
  }

  "The PlayParSeqImpl" should {

    "be able to convert to a ParSeq Task with given name" in {
      val name: String = "test"
      // Convert
      val helloTask: Task[String] = playParSeqImpl.toTask(name, () => Future { "Hello" })
      // Assert the name
      helloTask.getName must equalTo(name)
    }

    "be able to convert to a ParSeq Task with default name" in {
      // Convert
      val helloTask: Task[String] = playParSeqImpl.toTask(() => Future { "Hello" })
      // Assert the name
      helloTask.getName must equalTo(playParSeqImpl.DefaultTaskName)
    }

    "be able to convert to a ParSeq Task which can succeed" in {
      val test: String = "Test"
      val start: Int = test.length - 1
      // Convert then run
      val substringFuture: Future[String] = playParSeqImpl.runTask(
        playParSeqImpl.toTask(
          "substring",
          () => Future { test.substring(start) }))
      // Assert the result from the Future
      await(substringFuture) must equalTo(test.substring(start))
    }

    "be able to convert to a ParSeq Task which can recover" in {
      val test: String = "Test"
      val recover: String = "Recover"
      // With an invalid substring start index
      val start: Int = test.length + 1
      // Convert then run then recover
      val substringFuture: Future[String] = playParSeqImpl.runTask(
        playParSeqImpl.toTask(
          "substring",
          () => Future { test.substring(start) })
      ).recover { case t => recover }
      // Assert the result from the Future which should be the recover value
      await(substringFuture) must equalTo(recover)
    }

    "be able to convert to a ParSeq Task which can fail" in {
      val test: String = "Test"
      // With an invalid substring start index
      val start: Int = test.length + 1
      // Convert then run
      val substringFuture: Future[String] = playParSeqImpl.runTask(
        playParSeqImpl.toTask(
          "substring",
          () => Future { test.substring(start) }))
      // Assert the exception from the Future
      await(substringFuture) must throwA[StringIndexOutOfBoundsException]
    }

    "be able to run a ParSeq Task which can succeed" in {
      val test: String = "Test"
      val start: Int = test.length - 1
      // Run
      val substringFuture: Future[String] = playParSeqImpl.runTask(
        Task.callable(
          "substring",
          test.substring(start)))
      // Assert the result from the Future
      await(substringFuture) must equalTo(test.substring(start))
    }

    "be able to run a ParSeq Task which can recover" in {
      val test: String = "Test"
      val recover: String = "Recover"
      // With an invalid substring start index
      val start: Int = test.length + 1
      // Run then recover
      val substringFuture: Future[String] =
        playParSeqImpl.runTask(
          Task.callable(
            "substring",
            test.substring(start))
        ).recover { case t => recover }
      // Assert the result from the Future which should be the recover value
      await(substringFuture) must equalTo(recover)
    }

    "be able to run a ParSeq Task which can fail" in {
      val test: String = "Test"
      // With an invalid substring start index
      val start: Int = test.length + 1
      // Run
      val substringFuture: Future[String] =
        playParSeqImpl.runTask(
          Task.callable(
            "substring",
            test.substring(start)))
      // Assert the exception from the Future
      await(substringFuture) must throwA[StringIndexOutOfBoundsException]
    }
  }
}
