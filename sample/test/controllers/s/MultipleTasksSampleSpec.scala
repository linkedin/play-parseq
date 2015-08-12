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
 * The class MultipleTasksSampleSpec is a specification class for [[MultipleTasksSample]].
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
class MultipleTasksSampleSpec extends PlaySpecification {

  "The MultipleTasksSample" should {

    "respond to GET demo" in new WithApplication {
      val result = route(FakeRequest(GET, routes.MultipleTasksSample.demo().url)).get
      // Assert the status and the content
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      contentAsString(result).toInt must greaterThan(0)
    }
  }
}
