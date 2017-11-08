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
package com.linkedin.playparseq.trace.s.modules

import com.linkedin.playparseq.trace.s.{ParSeqTraceAction, ParSeqTraceBuilder, ParSeqTraceBuilderImpl}
import com.linkedin.playparseq.trace.s.renderers.{ParSeqTraceRenderer, ParSeqTraceRendererImpl}
import com.linkedin.playparseq.trace.s.sensors.{ParSeqTraceSensor, ParSeqTraceSensorImpl}
import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}


/**
 * The class ParSeqTraceModule is a preset Module for the dependency injection bindings of [[ParSeqTraceAction]].
 * This [[ParSeqTraceAction]] configuration includes [[ParSeqTraceBuilderImpl]], [[ParSeqTraceSensorImpl]] and
 * [[ParSeqTraceRendererImpl]].
 * The key `play.modules.enabled += "com.linkedin.playparseq.trace.s.modules.ParSeqTraceModule"` needs to be added into
 * your conf file, if you want to use this [[ParSeqTraceAction]] configuration.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
class ParSeqTraceModule extends Module {

  /**
   * The method bindings describes the bindings of the class [[ParSeqTraceAction]].
   *
   * @param environment The environment
   * @param configuration The configuration
   * @return A sequence of bindings
   */
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[ParSeqTraceBuilder].to[ParSeqTraceBuilderImpl],
    bind[ParSeqTraceSensor].to[ParSeqTraceSensorImpl],
    bind[ParSeqTraceRenderer].to[ParSeqTraceRendererImpl])

}
