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
package com.linkedin.playparseq.trace.j.modules;

import com.linkedin.playparseq.trace.j.ParSeqTraceAction;
import com.linkedin.playparseq.trace.j.ParSeqTraceBuilder;
import com.linkedin.playparseq.trace.j.ParSeqTraceBuilderImpl;
import com.linkedin.playparseq.trace.j.renderers.ParSeqTraceRenderer;
import com.linkedin.playparseq.trace.j.renderers.ParSeqTraceRendererImpl;
import com.linkedin.playparseq.trace.j.sensors.ParSeqTraceSensor;
import com.linkedin.playparseq.trace.j.sensors.ParSeqTraceSensorImpl;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;


/**
 * The class ParSeqTraceModule is a preset Module for the dependency injection bindings of {@link ParSeqTraceAction}.
 * This {@link ParSeqTraceAction} configuration includes {@link ParSeqTraceBuilderImpl}, {@link ParSeqTraceSensorImpl}
 * and {@link ParSeqTraceRendererImpl}.
 * The key `play.modules.enabled += "com.linkedin.playparseq.trace.j.modules.ParSeqTraceModule"` needs to be added into
 * your conf file, if you want to use this {@link ParSeqTraceAction} configuration.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public class ParSeqTraceModule extends Module {

  /**
   * The method bindings describes the bindings of the class {@link ParSeqTraceAction}.
   *
   * @param environment The environment
   * @param configuration The configuration
   * @return A sequence of bindings
   */
  @Override
  public Seq<Binding<?>> bindings(final Environment environment, final Configuration configuration) {
    return seq(
        bind(ParSeqTraceBuilder.class).to(ParSeqTraceBuilderImpl.class),
        bind(ParSeqTraceSensor.class).to(ParSeqTraceSensorImpl.class),
        bind(ParSeqTraceRenderer.class).to(ParSeqTraceRendererImpl.class));
  }

}
