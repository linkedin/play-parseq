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

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}


/**
 * The class Application is a Controller to show index page.
 *
 * @param controllerComponents The injected Controller component.
 * @author Yinan Ding (yding@linkedin.com)
 */
class Application @Inject()(controllerComponents: ControllerComponents) extends AbstractController(controllerComponents) {

  /**
   * The method index returns the index page.
   *
   * @return The Action for index page
   */
  def index = Action {
    Ok(views.html.index())
  }

}
