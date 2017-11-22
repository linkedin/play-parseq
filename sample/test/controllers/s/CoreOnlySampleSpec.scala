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
package controllers.s

import play.api.test.{FakeRequest, PlaySpecification, WithApplication}


/**
 * The class CoreOnlySampleSpec is a specification class for [[CoreOnlySample]].
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
class CoreOnlySampleSpec extends PlaySpecification {

  /**
   * The field TextText is the String for testing.
   */
  val TextText = "Test"

  /**
   * The field TestStartIndex is the start index of substring.
   */
  val TestStartIndex = 1

  /**
   * The field TestStartIndexFail is the start index of substring for failure.
   */
  val TestStartIndexFail = 5

  "The CoreOnlySample" should {
    "respond to GET input" in new WithApplication {
      val result = route(app, FakeRequest(GET, routes.CoreOnlySample.input().url)).get
      // Assert the status and the content
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must contain("Text")
    }

    "respond to GET demo" in new WithApplication {
      val result = route(app, FakeRequest(GET, routes.CoreOnlySample.demo(TextText, TestStartIndex).url)).get
      // Assert the status and the content
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      contentAsString(result) must equalTo(TextText.substring(TestStartIndex))
    }

    "respond to GET demo with invalid start index" in new WithApplication {
      val result = route(app, FakeRequest(GET, routes.CoreOnlySample.demo(TextText, TestStartIndexFail).url)).get
      // Assert the status and the content
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      contentAsString(result) must equalTo(new CoreOnlySample(null, null)(null).DefaultFailure)
    }
  }

}
