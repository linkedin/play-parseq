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
import play.api.libs.typedmap.TypedKey
import play.api.mvc.RequestHeader
import scala.collection.JavaConverters._
import scala.collection.mutable.{Set => MutableSet}


/**
 * The trait ParSeqTaskStore defines putting ParSeq Task into store and getting all Tasks out of store.
 * During a request, all ParSeq Tasks will be stored in the ParSeqTaskStore when they run, and will be retrieved when it
 * needs to generate the ParSeq Trace of the current request. The put/get APIs can only be properly used after the API
 * initialize is called for setting up the store.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
trait ParSeqTaskStore {

  /**
   * The method put puts ParSeq Task into store.
   *
   * @param task The ParSeq Task
   * @param requestHeader The Request
   */
  def put(task: Task[_])(implicit requestHeader: RequestHeader)

  /**
   * The method get gets all Tasks from one request out of store as an immutable Set.
   *
   * @param requestHeader The Request
   * @return A Set of Tasks
   */
  def get(implicit requestHeader: RequestHeader): Set[Task[_]]

  /**
   * The method initialize sets up the store properly for put/get APIs.
   *
   * @param request The origin Request
   * @tparam T The type parameter of the Request
   * @return The Request with store set up properly
   */
  def initialize[T <: RequestHeader](request: T): T

}

/**
 * The class ParSeqTaskStoreImpl is an implementation of the trait [[ParSeqTaskStore]], whose store exists inside the
 * attribute of the request.
 * However, the attribute is only initialized when you use the ParSeqTraceAction for the ParSeq Trace feature. The
 * store will still work correctly without ParSeqTraceAction when not using ParSeqTraceAction, but act like dummy.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
@Singleton
class ParSeqTaskStoreImpl extends ParSeqTaskStore {

  /**
   * The field ArgumentsKey is the default key of ParSeq Tasks.
   */
  val ArgumentsKey: TypedKey[MutableSet[Task[_]]] = TypedKey("ParSeqTasks")

  /**
   * @inheritdoc
   */
  override def put(task: Task[_])(implicit requestHeader: RequestHeader): Unit = getOption.map(_.add(task))

  /**
   * @inheritdoc
   */
  override def get(implicit requestHeader: RequestHeader): Set[Task[_]] = getOption.map(_.toSet).getOrElse(Set.empty)

  /**
   * @inheritdoc
   */
  override def initialize[T <: RequestHeader](request: T): T = request.addAttr(ArgumentsKey, Collections.newSetFromMap[Task[_]](new ConcurrentHashMap).asScala).asInstanceOf[T]

  /**
   * The method getOption gets the optional mutable Set of Tasks from one request out of store for modifications.
   *
   * @param requestHeader The Request
   * @return A Set of Tasks
   */
  private[this] def getOption(implicit requestHeader: RequestHeader) = requestHeader.attrs.get(ArgumentsKey)

}
