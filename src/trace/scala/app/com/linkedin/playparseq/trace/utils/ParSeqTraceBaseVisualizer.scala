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
package com.linkedin.playparseq.trace.utils

import com.linkedin.parseq.trace.Trace
import com.linkedin.parseq.trace.codec.json.JsonTraceCodec
import java.io.File
import play.api.Application
import scala.collection.immutable.ListMap
import scala.io.Source


/**
 * The class ParSeqTraceBaseVisualizer generates ParSeq TraceViewer HTML page with pre-fill script based on the ParSeq
 * `Trace`.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
abstract class ParSeqTraceBaseVisualizer {

  /**
   * The field TracevisRoot is the root path of all Trace resources.
   */
  val TracevisRoot = "/tracevis"

  /**
   * The field TraceName is the name of the Trace page.
   */
  val TraceName = "trace.html"

  /**
   * The method showTrace generates the HTML page based on the ParSeq `Trace`.
   *
   * @param trace The ParSeq Trace
   * @param application The Application
   * @return The HTML page
   */
  protected[this] def showTrace(trace: Trace, application: Application): String = {
    // Get Trace JSON
    val traceJson = new JsonTraceCodec().encode(trace)
    // Generate pre-fill script for onload Trace JSON
    val preFillScript =
      """
        |<base href="%s">
        |<script>
        |  var ESC_FLAGS = "gi";
        |  var EMBED_ESCAPES = __EMBED_ESCAPES__;
        |  var unescapeForEmbedding = function (str) {
        |    for (var key in EMBED_ESCAPES) {
        |      if (EMBED_ESCAPES.hasOwnProperty(key)) {
        |        str = str.replace(new RegExp(EMBED_ESCAPES[key], ESC_FLAGS), key);
        |      }
        |    }
        |    return str;
        |  };
        |  var getEmbeddedContent = function(id) {
        |    var contentElem = document.getElementById(id);
        |    var innerContent = contentElem.firstChild.nodeValue;
        |    return JSON.parse(unescapeForEmbedding(innerContent));
        |  };
        |  window.onload = function() {
        |    var json = getEmbeddedContent('injected-json');
        |    // The renderTrace method does not yet support normal JS objects, but expects stringified JSON
        |    renderTrace(JSON.stringify(json));
        |  }
        |</script>
      """.stripMargin.format(TracevisRoot + "/")
    // Generate injected JSON placeholder
    val injectedJson = """<code id="injected-json"><!--__JSON__--></code>"""
    // Build HTML page
    application.resourceAsStream(new File(TracevisRoot, TraceName).getPath).map(stream => {
      // Escape script and JSON
      val script = preFillScript.replace("__EMBED_ESCAPES__", """{"&":"&amp;","-":"&dsh;"}""")
      val json = injectedJson.replace("__JSON__", ListMap("&" -> "&amp;", "-" -> "&dsh;").foldLeft(traceJson)((acc, escape) => acc.replaceAll(escape._1, escape._2)))
      // Inject script and JSON
      Source.fromInputStream(stream).mkString.replace("<title>", script + "\n<title>").replace("</style>", "</style>\n" + json)
    }).orNull
  }
}
