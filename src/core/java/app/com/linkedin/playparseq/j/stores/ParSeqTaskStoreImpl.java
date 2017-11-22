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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Singleton;
import play.libs.typedmap.TypedKey;
import play.mvc.Http;


/**
 * The class ParSeqTaskStoreImpl is an implementation of the interface {@link ParSeqTaskStore}, whose store exists
 * inside the attribute of the request.
 * However, the attribute is only initialized when you use the ParSeqTraceAction for the ParSeq Trace feature. The
 * store will still work correctly without ParSeqTraceAction when not using ParSeqTraceAction, but act like dummy.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
public class ParSeqTaskStoreImpl implements ParSeqTaskStore {

  /**
   * The field ARGUMENTS_KEY is the default key of ParSeq Tasks.
   */
  public final static TypedKey<Set<Task<?>>> ARGUMENTS_KEY = TypedKey.create("ParSeqTasks");

  /**
   * {@inheritDoc}
   */
  @Override
  public void put(final Http.Context context, final Task<?> task) {
    getOptional(context).map(tasks -> tasks.add(task));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Task<?>> get(final Http.Context context) {
    return getOptional(context).map(Collections::unmodifiableSet).orElse(Collections.emptySet());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Http.Context initialize(final Http.Context context) {
    return context.withRequest(context.request().addAttr(ARGUMENTS_KEY, ConcurrentHashMap.newKeySet()));
  }

  /**
   * The method getOptional gets the optional modifiable Set of Tasks from one request out of store for modifications.
   *
   * @param context The HTTP Context
   * @return A Set of Tasks
   */
  private Optional<Set<Task<?>>> getOptional(final Http.Context context) {
    return context.request().attrs().getOptional(ARGUMENTS_KEY);
  }

}
