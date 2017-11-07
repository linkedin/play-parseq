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
package com.linkedin.playparseq.s.stores

import com.linkedin.parseq.Task
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Singleton
import play.api.mvc.{Request, RequestHeader, WrappedRequest}
import scala.collection.JavaConverters._
import scala.collection.concurrent.TrieMap
import scala.collection.mutable


/**
 * The trait ParSeqTaskStore defines putting ParSeq Task into store and getting all Tasks out of store.
 * During a request, the ParSeqTaskStore will store all ParSeq Tasks when they run. The ParSeqTaskStore will retrieve
 * all the Tasks only when it needs to generate the ParSeq Trace of the current request.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
trait ParSeqTaskStore {

  /**
   * The method put puts ParSeq Task into store.
   *
   * @param task The ParSeq Task
   * @param requestHeader The request
   */
  def put(task: Task[_])(implicit requestHeader: RequestHeader)

  /**
   * The method get gets all Tasks from one request out of store as a Set.
   *
   * @param requestHeader The request
   * @return A set of Tasks
   */
  def get(implicit requestHeader: RequestHeader): mutable.Set[Task[_]]

}

/**
 * The class ParSeqTaskStoreImpl is an implementation of the trait [[ParSeqTaskStore]], whose store exists inside the
 * [[ContextRequest]].
 * However, the [[ContextRequest]] is only used when you use the ParSeqTraceAction for the ParSeq Trace feature. The
 * store will still work correctly without [[ContextRequest]] when not using ParSeqTraceAction, but act like dummy.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
class ParSeqTaskStoreImpl extends ParSeqTaskStore {

  /**
   * The field ArgumentsKey is the default key of ParSeq Tasks.
   */
  val ArgumentsKey = "ParSeqTasks"

  /**
   * @inheritdoc
   */
  override def put(task: Task[_])(implicit requestHeader: RequestHeader): Unit = {
    requestHeader match {
      case _: ContextRequest[_] => get.add(task)
      case _ =>
    }
  }

  /**
   * @inheritdoc
   */
  override def get(implicit requestHeader: RequestHeader): mutable.Set[Task[_]] = {
    requestHeader match {
      case ctx: ContextRequest[_] => ctx.args.get(ArgumentsKey).map(_.asInstanceOf[mutable.Set[Task[_]]])
        .getOrElse({
        val tasks: mutable.Set[Task[_]] = Collections.newSetFromMap[Task[_]](new ConcurrentHashMap).asScala
        ctx.args.putIfAbsent(ArgumentsKey, tasks).getOrElse(tasks).asInstanceOf[mutable.Set[Task[_]]]
      })
      case _ => mutable.Set.empty
    }
  }

}

/**
 * The class ContextRequest is a WrappedRequest which wraps a request with an arguments TrieMap for storing data within
 * the scope of request.
 *
 * @param args The arguments TrieMap for storing data
 * @param request The origin request
 * @tparam T The type parameter of the Request
 * @author Yinan Ding (yding@linkedin.com)
 */
class ContextRequest[T](val args: TrieMap[String, Any], request: Request[T]) extends WrappedRequest[T](request)
