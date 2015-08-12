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
package com.linkedin.playparseq.j.stores;

import com.linkedin.parseq.Task;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Singleton;
import play.mvc.Http;


/**
 * The class ParSeqTaskStoreImpl is an implementation of the interface {@link ParSeqTaskStore}, whose store exists
 * inside the current HTTP Context.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
public class ParSeqTaskStoreImpl implements ParSeqTaskStore {

  /**
   * The field ARGUMENTS_KEY is the default key of ParSeq Tasks.
   */
  public final static String ARGUMENTS_KEY = "ParSeqTasks";

  @Override
  public void put(final Task<?> value) {
    this.get().add(value);
  }

  @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter", "unchecked"})
  @Override
  public Set<Task<?>> get() {
    Map<String, Object> args = Http.Context.current().args;
    synchronized (args) {
      return Optional.ofNullable((Set<Task<?>>) args.get(ARGUMENTS_KEY)).orElseGet(() -> {
        Set<Task<?>> tasks = Collections.newSetFromMap(new ConcurrentHashMap());
        args.put(ARGUMENTS_KEY, tasks);
        return tasks;
      });
    }
  }
}
