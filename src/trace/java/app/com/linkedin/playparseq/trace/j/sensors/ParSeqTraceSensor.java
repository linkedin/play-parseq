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
package com.linkedin.playparseq.trace.j.sensors;

import com.linkedin.playparseq.j.stores.ParSeqTaskStore;
import play.mvc.Http;


/**
 * The interface ParSeqTraceSensor defines deciding whether to display ParSeq Trace data or the origin Result based on
 * {@link Http.Context} and {@link ParSeqTaskStore}.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public interface ParSeqTraceSensor {

  /**
   * The method isEnabled decides whether ParSeq Trace is enabled or not from {@link Http.Context} and
   * {@link ParSeqTaskStore}.
   *
   * @param context The HTTP Context
   * @param parSeqTaskStore The {@link ParSeqTaskStore} for getting ParSeq Tasks
   * @return The decision
   */
  boolean isEnabled(final Http.Context context, final ParSeqTaskStore parSeqTaskStore);

}
