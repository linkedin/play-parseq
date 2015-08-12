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
package com.linkedin.playparseq.s.modules

import com.linkedin.parseq.Engine
import com.linkedin.playparseq.s.{PlayParSeq, PlayParSeqImpl}
import com.linkedin.playparseq.s.stores.{ParSeqTaskStore, ParSeqTaskStoreImpl}
import com.linkedin.playparseq.utils.{EngineProvider, PlayParSeqHelper}
import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}


/**
 * The class PlayParSeqModule is a preset Module for the dependency injection bindings of [[PlayParSeq]].
 * This [[PlayParSeq]] configuration includes [[PlayParSeqImpl]], [[ParSeqTaskStoreImpl]], [[EngineProvider]] and
 * [[PlayParSeqHelper]].
 * The key `play.modules.enabled += "com.linkedin.playparseq.s.modules.PlayParSeqModule"` needs to be added into your
 * conf file, if you want to use this [[PlayParSeq]] configuration.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
class PlayParSeqModule extends Module {

  /**
   * The method bindings describes the bindings of the trait [[PlayParSeq]].
   *
   * @param environment The environment
   * @param configuration The configuration
   * @return A sequence of bindings
   */
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[PlayParSeq].to[PlayParSeqImpl],
    bind[ParSeqTaskStore].to[ParSeqTaskStoreImpl],
    bind[Engine].toProvider[EngineProvider])
}
