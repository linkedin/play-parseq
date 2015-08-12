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
package com.linkedin.playparseq.j.modules;

import com.linkedin.parseq.Engine;
import com.linkedin.playparseq.j.PlayParSeq;
import com.linkedin.playparseq.j.PlayParSeqImpl;
import com.linkedin.playparseq.j.stores.ParSeqTaskStore;
import com.linkedin.playparseq.j.stores.ParSeqTaskStoreImpl;
import com.linkedin.playparseq.utils.EngineProvider;
import com.linkedin.playparseq.utils.PlayParSeqHelper;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;


/**
 * The class PlayParSeqModule is a preset Module for the dependency injection bindings of {@link PlayParSeq}.
 * This {@link PlayParSeq} configuration includes {@link PlayParSeqImpl}, {@link ParSeqTaskStoreImpl},
 * {@link EngineProvider} and {@link PlayParSeqHelper}.
 * The key `play.modules.enabled += "com.linkedin.playparseq.j.modules.PlayParSeqModule"` needs to be added into your
 * conf file, if you want to use this {@link PlayParSeq} configuration.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public class PlayParSeqModule extends Module {

  /**
   * The method bindings describes the bindings of the interface {@link PlayParSeq}.
   *
   * @param environment The environment
   * @param configuration The configuration
   * @return A sequence of bindings
   */
  @Override
  public Seq<Binding<?>> bindings(final Environment environment, final Configuration configuration) {
    return seq(
        bind(PlayParSeq.class).to(PlayParSeqImpl.class),
        bind(ParSeqTaskStore.class).to(ParSeqTaskStoreImpl.class),
        bind(Engine.class).toProvider(EngineProvider.class));
  }
}
