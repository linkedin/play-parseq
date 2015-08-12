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


/**
 * The interface ParSeqTaskStore defines putting ParSeq Task into store and getting Tasks out of store.
 * During a request, the ParSeqTaskStore will store all ParSeq Tasks when they run. The ParSeqTaskStore will retrieve
 * all the Tasks only when it needs to generate the ParSeq Trace of the current request.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public interface ParSeqTaskStore {

  /**
   * The method put puts ParSeq Task into store.
   *
   * @param task The ParSeq Task
   */
  void put(final Task<?> task);

  /**
   * The method get gets all Tasks from one request out of store as a Set.
   *
   * @return A set of Tasks
   */
  Set<Task<?>> get();
}
