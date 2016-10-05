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
import play.Environment;
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
   * The field _environment is a Play Environment for checking dev mode.
   */
  private final Environment _environment;

  /**
   * The constructor injects the Environment.
   *
   * @param environment The injected Environment component
   */
  @Inject
  public ParSeqTraceSensorImpl(final Environment environment) {
    this._environment = environment;
  }

  @Override
  public boolean isEnabled(final ParSeqTaskStore parSeqTaskStore, final Http.Context context) {
    return _environment.isDev() &&
        Optional.ofNullable(context.request().getQueryString(QUERY_KEY)).map(s -> s.equals("true")).orElse(false) &&
        parSeqTaskStore.get(context).size() > 0;
  }
}
