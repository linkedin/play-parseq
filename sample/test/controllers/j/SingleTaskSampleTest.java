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
package controllers.j;

import org.junit.Test;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;


/**
 * The class SingleTaskSampleTest is a test class for {@link SingleTaskSample}.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public class SingleTaskSampleTest extends WithApplication {

  /**
   * The method canGetDemo tests the ability of GET demo.
   */
  @Test
  public void canGetDemo() {
    Result result = route(routes.SingleTaskSample.demo());
    // Assert the status and the content
    assertEquals(OK, result.status());
    assertEquals("text/plain", result.contentType().orElse(""));
    assertTrue(contentAsString(result).equals("Hello World"));
  }

}
