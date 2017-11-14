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
import java.util.Set;
import play.mvc.Http;


/**
 * The interface ParSeqTaskStore defines putting ParSeq Task into store and getting Tasks out of store.
 * During a request, all ParSeq Tasks will be stored in the ParSeqTaskStore when they run, and will be retrieved when it
 * needs to generate the ParSeq Trace of the current request. The put/get APIs can only be properly used after the API
 * initialize is called for setting up the store.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public interface ParSeqTaskStore {

  /**
   * The method put puts ParSeq Task into store.
   *
   * @param context The HTTP Context
   * @param task The ParSeq Task
   */
  void put(final Http.Context context, final Task<?> task);

  /**
   * The method get gets all Tasks from one request out of store as an unmodifiable Set.
   *
   * @param context The HTTP Context
   * @return A set of Tasks
   */
  Set<Task<?>> get(final Http.Context context);

  /**
   * The method initialize sets up the store properly for put/get APIs.
   *
   * @param context The origin HTTP Context
   * @return The HTTP Context with store set up properly
   */
  Http.Context initialize(final Http.Context context);

}
