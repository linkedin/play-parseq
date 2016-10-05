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
package com.linkedin.playparseq.trace.j.renderers;

import com.linkedin.playparseq.j.stores.ParSeqTaskStore;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;


/**
 * The interface ParSeqTraceRenderer defines rendering a {@code F.Promise<Result>} from {@link ParSeqTaskStore}.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public interface ParSeqTraceRenderer {

  /**
   * The method render generates a {@code F.Promise<Result>} from {@link ParSeqTaskStore}.
   *
   * @param parSeqTaskStore The {@link ParSeqTaskStore} for getting ParSeq Tasks
   * @param context The HTTP Context
   * @return The Play Promise of Result
   */
  F.Promise<Result> render(final ParSeqTaskStore parSeqTaskStore, final Http.Context context);
}
