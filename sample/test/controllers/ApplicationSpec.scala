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
package controllers

import play.api.test.{FakeRequest, PlaySpecification, WithApplication}

/**
 * The class ApplicationSpec is a specification class for [[Application]].
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
class ApplicationSpec extends PlaySpecification {

  "The Application" should {

    "respond to GET index" in new WithApplication {
      val result = route(FakeRequest(GET, routes.Application.index().url)).get
      // Assert the status and the content
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must contain("Sample")
    }
  }
}
