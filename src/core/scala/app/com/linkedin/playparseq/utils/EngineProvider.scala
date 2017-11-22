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
package com.linkedin.playparseq.utils

import com.linkedin.parseq.{Engine, EngineBuilder}
import javax.inject.{Inject, Provider, Singleton}
import java.util.concurrent.{ExecutorService, Executors, ScheduledExecutorService, TimeUnit}
import play.api.Configuration
import play.api.inject.ApplicationLifecycle
import scala.concurrent.{ExecutionContext, Future}


/**
 * The class EngineProvider is a preset Provider which provides a ParSeq Engine for dependency injection.
 * The EngineProvider will try to load the key-value of `parseq.engine.numThreads` (The number of threads in Engine's
 * pool) and `parseq.engine.terminationWaitSeconds` (The maximum time to wait for Engine's termination in the unit of
 * seconds) from your conf file, otherwise it will use the default values.
 *
 * @param applicationLifecycle The injected ApplicationLifecycle component
 * @param configuration The injected Configuration component
 * @param executionContext The injected [[ExecutionContext]] component
 * @see <a href="https://github.com/linkedin/parseq/wiki/User's-Guide#creating-an-engine">ParSeq Wiki</a>
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
class EngineProvider @Inject()(applicationLifecycle: ApplicationLifecycle, configuration: Configuration)(implicit executionContext: ExecutionContext) extends Provider[Engine] {

  /**
   * The field taskScheduler is a task scheduler for ParSeq Engine.
   */
  private[this] val taskScheduler: ExecutorService = Executors.newFixedThreadPool(getNumThreads)

  /**
   * The field timerScheduler is a timer scheduler for ParSeq Engine.
   */
  private[this] val timerScheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor

  /**
   * The field engine is the ParSeq Engine to be provided to the injector.
   */
  private[this] val engine: Engine = new EngineBuilder().setTaskExecutor(taskScheduler).setTimerScheduler(timerScheduler).build

  // Setup
  applicationLifecycle.addStopHook(() => Future {
    // Tear down the ParSeq Engine
    engine.shutdown()
    engine.awaitTermination(getTerminationWaitSeconds, TimeUnit.SECONDS)
    taskScheduler.shutdown()
    timerScheduler.shutdown()
  })

  /**
   * The method get gets the ParSeq Engine.
   *
   * @return The ParSeq Engine
   */
  override def get(): Engine = engine

  /**
   * The method getNumThreads gets the number of threads in Engine's pool. It will load from conf file, otherwise it
   * will use a default value, which is the number of available processors plus 1.
   *
   * @return The number of threads
   */
  private[this] def getNumThreads: Int = configuration.getOptional[Int]("parseq.engine.numThreads").getOrElse(Runtime.getRuntime.availableProcessors + 1)

  /**
   * The method getTerminationWaitSeconds gets the maximum time to wait for Engine's termination in the unit of seconds.
   * It will load from conf file, otherwise it will use a default value, which is 1.
   *
   * @return The time to wait in seconds
   */
  private[this] def getTerminationWaitSeconds: Int = configuration.getOptional[Int]("parseq.engine.terminationWaitSeconds").getOrElse(1)

}
