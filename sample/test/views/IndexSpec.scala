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
package views

import play.api.test.{PlaySpecification, WithApplication}
import play.twirl.api.Content


/**
 * The class IndexSpec is a specification class for index view template.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
class IndexSpec extends PlaySpecification {

  "The Index" should {
    "be able to render" in new WithApplication {
      val html = views.html.index().asInstanceOf[Content]
      // Assert the content type and the content
      contentType(html) must equalTo("text/html")
      contentAsString(html) must contain("Sample")
    }
  }

}
