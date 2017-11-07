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
 * The class CoreOnlySampleTest is a test class for {@link CoreOnlySample}.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public class CoreOnlySampleTest extends WithApplication {

  /**
   * The field TEST_TEXT is the String for testing.
   */
  public final static String TEST_TEXT = "Test";

  /**
   * The field TEST_START is the start index of substring.
   */
  public final static int TEST_START = 1;

  /**
   * The field TEST_START_FAIL is the start index of substring for failure.
   */
  public final static int TEST_START_FAIL = 5;

  /**
   * The method canGetInput tests the ability of GET input.
   */
  @Test
  public void canGetInput() {
    Result result = route(routes.CoreOnlySample.input());
    // Assert the status and the content
    assertEquals(OK, result.status());
    assertEquals("text/html", result.contentType().orElse(""));
    assertTrue(contentAsString(result).contains("Text"));
  }

  /**
   * The method canGetDemo tests the ability of GET demo.
   */
  @Test
  public void canGetDemo() {
    Result result = route(routes.CoreOnlySample.demo(TEST_TEXT, TEST_START));
    // Assert the status and the content
    assertEquals(OK, result.status());
    assertEquals("text/plain", result.contentType().orElse(""));
    assertEquals(contentAsString(result), TEST_TEXT.substring(TEST_START));
  }

  /**
   * The method canGetDemoWithFailure tests the ability of GET demo with invalid start index.
   */
  @Test
  public void canGetDemoWithFailure() {
    Result result = route(routes.CoreOnlySample.demo(TEST_TEXT, TEST_START_FAIL));
    // Assert the status and the content
    assertEquals(OK, result.status());
    assertEquals("text/plain", result.contentType().orElse(""));
    assertEquals(contentAsString(result), CoreOnlySample.DEFAULT_FAILURE);
  }

}
