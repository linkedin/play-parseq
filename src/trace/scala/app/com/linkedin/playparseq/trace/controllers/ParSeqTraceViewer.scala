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
package com.linkedin.playparseq.trace.controllers

import com.linkedin.parseq.{Engine, GraphvizEngine, HttpResponse, Task}
import com.linkedin.playparseq.s.PlayParSeqImplicits._
import com.linkedin.playparseq.utils.PlayParSeqHelper
import java.io.ByteArrayInputStream
import java.nio.file.{Files, Path}

import javax.inject.{Inject, Singleton}
import javax.servlet.http.HttpServletResponse
import org.apache.commons.io.FileUtils
import play.api.Configuration
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{Action, AnyContent, Controller, Result}

import scala.concurrent.Future
import scala.concurrent.Promise
import scala.sys.process


/**
 * The class ParSeqTraceViewer is a Controller to generate ParSeq Trace page with dot file and manage all the ParSeq
 * Trace resources.
 *
 * @param engine The injected ParSeq Engine component
 * @param applicationLifecycle The injected ApplicationLifeCycle component
 * @param configuration The injected Configuration component
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
class ParSeqTraceViewer @Inject()(engine: Engine, applicationLifecycle: ApplicationLifecycle, configuration: Configuration) extends PlayParSeqHelper with Controller {
  private val Log = Logger(this.getClass())
  /**
   * The field cachePath is the file path of the ParSeq Trace cache directory.
   */
  lazy val cachePath: Path = Files.createTempDirectory("cache")

  /**
   * The field graphvizEngine is for generating graphviz files.
   */
  lazy val graphvizEngine: GraphvizEngine = new GraphvizEngine(getDotLocation, cachePath, getCacheSize, getTimeoutMilliseconds, getParallelLevel, getDelayMilliseconds, getProcessQueueSize)

  /**
   * The field setup is for starting the GraphvizEngine and hooking cleanup to application lifecycle.
   */
  lazy val setup = {
    // Start the GraphvizEngine
    graphvizEngine.start()
    // Add stop hook
    applicationLifecycle.addStopHook(() => Future {
      // Stop the GraphvizEngine
      graphvizEngine.stop()
      // Clear cache directory
      FileUtils.deleteDirectory(cachePath.toFile)
    })
  }

  /**
   * The method at returns the ParSeq Trace resource file.
   *
   * @param file The file name
   * @return The Action
   */
  def at(file: String): Action[AnyContent] = {
    setup
    if (file.startsWith("cache")) {
      // Cache file
      Action {
        Ok.sendFile(cachePath.resolve(file.split("/")(1)).toFile)
      }
    } else {
      // Resource file
      controllers.Assets.at("/tracevis", file)
    }
  }

  /**
   * The method dot generates graphviz files and returns the build response as result.
   *
   * @return The Action
   */
  def dot: Action[AnyContent] = Action.async(request => {
    // Get hash value
    val hash = request.getQueryString("hash").orNull
    // Get body info
    val body = request.body.asText.map((b) => new ByteArrayInputStream(b.getBytes)).orNull
    // Build files
    val task: Task[Result] = graphvizEngine.build(hash, body).map((response: HttpResponse) => {
      // Generate Result
      response.getStatus.intValue match {
        case HttpServletResponse.SC_OK => Ok(response.getBody)
        case HttpServletResponse.SC_BAD_GATEWAY => BadRequest(response.getBody)
        case _ => InternalServerError(response.getBody)
      }
    })
    // Run task
    val promise: Promise[Result] = Promise()
    engine.run(bindTaskToPromise(task, promise))
    promise.future
  })

  /**
   * The methods getDotLocation gets the file path of the dot from conf file, otherwise it will get from the system.
   *
   * @return The file path
   */
  private[this] def getDotLocation: String = configuration.getString("parseq.trace.docLocation")
    .getOrElse(
      try {
        System.getProperty("os.name").toLowerCase match {
          case u if u.indexOf("mac") >=0 || u.indexOf("nix") >= 0 || u.indexOf("nux") >= 0 || u.indexOf("aix") >= 0 => process.stringToProcess("which dot").!!.trim
          case w if w.indexOf("win") >= 0 => process.stringToProcess("where dot").!!.trim
          case _ => ""
        }
      } catch {
        case e: Exception =>
          val ret = "No executable for dot found. See http://www.graphviz.org"
          Log.error(ret)
          ret
      }
    )

  /**
   * The method getCacheSize gets the number of cache items in the GraphvizEngine from conf file, otherwise it will
   * generate a default value, which is 1024.
   *
   * @return The number of cache
   */
  private[this] def getCacheSize: Int = configuration.getInt("parseq.trace.cacheSize")
    .getOrElse(1024)

  /**
   * The method getTimeoutSeconds gets the timeout of the GraphvizEngine execution in the unit of milliseconds from conf
   * file, otherwise it will generate a default value, which is 5000.
   *
   * @return The timeout in milliseconds
   */
  private[this] def getTimeoutMilliseconds: Long = configuration.getLong("parseq.trace.timeoutMilliseconds")
    .getOrElse(5000)

  /**
   * The method getParallelLevel gets the maximum of the GraphvizEngine's parallel level from conf file, otherwise it
   * will generate a default value, which is the number of available processors.
   *
   * @return The parallel level
   */
  private[this] def getParallelLevel: Int = configuration.getInt("parseq.trace.parallelLevel")
    .getOrElse(Runtime.getRuntime.availableProcessors)

  /**
   * The method getDelayMilliseconds gets the delay time between different executions of the GraphvizEngine in the unit
   * of milliseconds from conf file, otherwise it will generate a default value, which is 5.
   *
   * @return The delay time in milliseconds
   */
  private[this] def getDelayMilliseconds: Long = configuration.getLong("parseq.trace.delayMilliseconds")
    .getOrElse(5)

  /**
   * The method getProcessQueueSize gets the size of the GraphvizEngine's process queue from conf file, otherwise it
   * will generate a default value, which is 1000.
   *
   * @return The size of process queue
   */
  private[this] def getProcessQueueSize: Int = configuration.getInt("parseq.trace.processQueueSize")
    .getOrElse(1000)
}
