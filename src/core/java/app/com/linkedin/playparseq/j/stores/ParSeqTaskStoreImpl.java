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
 * inside the HTTP Context's args map.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
public class ParSeqTaskStoreImpl implements ParSeqTaskStore {

  /**
   * The field ARGUMENTS_KEY is the default key of ParSeq Tasks.
   */
  public final static String ARGUMENTS_KEY = "ParSeqTasks";

  /**
   * {@inheritDoc}
   */
  @Override
  public void put(final Http.Context context, final Task<?> task) {
    get(context).add(task);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public Set<Task<?>> get(final Http.Context context) {
    Map<String, Object> args = context.args;
    synchronized (args) {
      return Optional.ofNullable((Set<Task<?>>) args.get(ARGUMENTS_KEY)).orElseGet(() -> {
        Set<Task<?>> tasks = Collections.newSetFromMap(new ConcurrentHashMap());
        args.put(ARGUMENTS_KEY, tasks);
        return tasks;
      });
    }
  }

}
