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
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import play.Application;
import play.mvc.Http;


/**
 * The class ParSeqTraceSensorImpl is an implementation of the interface {@link ParSeqTraceSensor}.
 * It determines based on whether the application is under dev mode, whether the query param is present, and whether the
 * data from the {@link ParSeqTaskStore} is available.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
public class ParSeqTraceSensorImpl implements ParSeqTraceSensor {

  /**
   * The field QUERY_KEY is the key of query for ParSeq Trace.
   */
  public final static String QUERY_KEY = "parseq-trace";

  /**
   * The field _application is a Play Application for checking dev mode.
   */
  private final Application _application;

  /**
   * The constructor injects the Application.
   *
   * @param application The injected Application component
   */
  @Inject
  public ParSeqTraceSensorImpl(final Application application) {
    this._application = application;
  }

  @Override
  public boolean isEnabled(final Http.Context ctx, final ParSeqTaskStore parSeqTaskStore) {
    return _application.isDev() &&
        Optional.ofNullable(ctx.request().getQueryString(QUERY_KEY)).map(s -> s.equals("true")).orElse(false) &&
        parSeqTaskStore.get().size() > 0;
  }
}
