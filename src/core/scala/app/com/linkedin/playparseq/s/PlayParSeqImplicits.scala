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
package com.linkedin.playparseq.s

import com.linkedin.parseq.function._
import java.util.concurrent.Callable
import java.util.function.{Consumer, Supplier}
import scala.language.implicitConversions


/**
 * The object PlayParSeqImplicits defines some implicit methods which are useful for [[PlayParSeq]].
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
object PlayParSeqImplicits {

  /**
   * The method toConsumer1 converts a function `(T1) => Unit` to a ParSeq `Consumer1[T1]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @return The ParSeq Consumer1
   */
  implicit def toConsumer1[T1](f: (T1) => Unit): Consumer1[T1] =
    new Consumer1[T1] {
      override def accept(t1: T1): Unit = f(t1)
    }

  /**
   * The method toConsumer2 converts a function `(T1, T2) => Unit` to a ParSeq `Consumer2[T1, T2]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @return The ParSeq Consumer2
   */
  implicit def toConsumer2[T1, T2](f: (T1, T2) => Unit): Consumer2[T1, T2] =
    new Consumer2[T1, T2] {
      override def accept(t1: T1, t2: T2): Unit = f(t1, t2)
    }

  /**
   * The method toConsumer3 converts a function `(T1, T2, T3) => Unit` to a ParSeq `Consumer3[T1, T2, T3]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @return The ParSeq Consumer3
   */
  implicit def toConsumer3[T1, T2, T3](f: (T1, T2, T3) => Unit): Consumer3[T1, T2, T3] =
    new Consumer3[T1, T2, T3] {
      override def accept(t1: T1, t2: T2, t3: T3): Unit = f(t1, t2, t3)
    }

  /**
   * The method toConsumer4 converts a function `(T1, T2, T3, T4) => Unit` to a ParSeq `Consumer4[T1, T2, T3, T4]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @tparam T4 The type parameter of the fourth function parameter
   * @return The ParSeq Consumer4
   */
  implicit def toConsumer4[T1, T2, T3, T4](f: (T1, T2, T3, T4) => Unit): Consumer4[T1, T2, T3, T4] =
    new Consumer4[T1, T2, T3, T4] {
      override def accept(t1: T1, t2: T2, t3: T3, t4: T4): Unit = f(t1, t2, t3, t4)
    }

  /**
   * The method toConsumer5 converts a function `(T1, T2, T3, T4, T5) => Unit` to a ParSeq
   * `Consumer5[T1, T2, T3, T4, T5]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @tparam T4 The type parameter of the fourth function parameter
   * @tparam T5 The type parameter of the fifth function parameter
   * @return The ParSeq Consumer5
   */
  implicit def toConsumer5[T1, T2, T3, T4, T5](f: (T1, T2, T3, T4, T5) => Unit): Consumer5[T1, T2, T3, T4, T5] =
    new Consumer5[T1, T2, T3, T4, T5] {
      override def accept(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5): Unit = f(t1, t2, t3, t4, t5)
    }

  /**
   * The method toConsumer6 converts a function `(T1, T2, T3, T4, T5, T6) => Unit` to a ParSeq
   * `Consumer6[T1, T2, T3, T4, T5, T6]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @tparam T4 The type parameter of the fourth function parameter
   * @tparam T5 The type parameter of the fifth function parameter
   * @tparam T6 The type parameter of the sixth function parameter
   * @return The ParSeq Consumer6
   */
  implicit def toConsumer6[T1, T2, T3, T4, T5, T6](f: (T1, T2, T3, T4, T5, T6) => Unit): Consumer6[T1, T2, T3, T4, T5, T6] =
    new Consumer6[T1, T2, T3, T4, T5, T6] {
      override def accept(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6): Unit = f(t1, t2, t3, t4, t5, t6)
    }

  /**
   * The method toConsumer7 converts a function `(T1, T2, T3, T4, T5, T6, T7) => Unit` to a ParSeq
   * `Consumer7[T1, T2, T3, T4, T5, T6, T7]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @tparam T4 The type parameter of the fourth function parameter
   * @tparam T5 The type parameter of the fifth function parameter
   * @tparam T6 The type parameter of the sixth function parameter
   * @tparam T7 The type parameter of the seventh function parameter
   * @return The ParSeq Consumer7
   */
  implicit def toConsumer7[T1, T2, T3, T4, T5, T6, T7](f: (T1, T2, T3, T4, T5, T6, T7) => Unit): Consumer7[T1, T2, T3, T4, T5, T6, T7] =
    new Consumer7[T1, T2, T3, T4, T5, T6, T7] {
      override def accept(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7): Unit = f(t1, t2, t3, t4, t5, t6, t7)
    }

  /**
   * The method toConsumer8 converts a function `(T1, T2, T3, T4, T5, T6, T7, T8) => Unit` to a ParSeq
   * `Consumer8[T1, T2, T3, T4, T5, T6, T7, T8]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @tparam T4 The type parameter of the fourth function parameter
   * @tparam T5 The type parameter of the fifth function parameter
   * @tparam T6 The type parameter of the sixth function parameter
   * @tparam T7 The type parameter of the seventh function parameter
   * @tparam T8 The type parameter of the eighth function parameter
   * @return The ParSeq Consumer8
   */
  implicit def toConsumer8[T1, T2, T3, T4, T5, T6, T7, T8](f: (T1, T2, T3, T4, T5, T6, T7, T8) => Unit): Consumer8[T1, T2, T3, T4, T5, T6, T7, T8] =
    new Consumer8[T1, T2, T3, T4, T5, T6, T7, T8] {
      override def accept(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8): Unit = f(t1, t2, t3, t4, t5, t6, t7, t8)
    }

  /**
   * The method toConsumer9 converts a function `(T1, T2, T3, T4, T5, T6, T7, T8, T9) => Unit` to a ParSeq
   * `Consumer9[T1, T2, T3, T4, T5, T6, T7, T8, T9]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @tparam T4 The type parameter of the fourth function parameter
   * @tparam T5 The type parameter of the fifth function parameter
   * @tparam T6 The type parameter of the sixth function parameter
   * @tparam T7 The type parameter of the seventh function parameter
   * @tparam T8 The type parameter of the eighth function parameter
   * @tparam T9 The type parameter of the ninth function parameter
   * @return The ParSeq Consumer9
   */
  implicit def toConsumer9[T1, T2, T3, T4, T5, T6, T7, T8, T9](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => Unit): Consumer9[T1, T2, T3, T4, T5, T6, T7, T8, T9] =
    new Consumer9[T1, T2, T3, T4, T5, T6, T7, T8, T9] {
      override def accept(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9): Unit = f(t1, t2, t3, t4, t5, t6, t7, t8, t9)
    }

  /**
   * The method toFunction1 converts a function `(T1) => R` to a ParSeq `Function1[T1, R]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam R The type parameter of the function and the ParSeq Function1
   * @return The ParSeq Function1
   */
  implicit def toFunction1[T1, R](f: (T1) => R): Function1[T1, R] =
    new Function1[T1, R] {
      override def apply(t1: T1): R = f(t1)
    }

  /**
   * The method toFunction2 converts a function `(T1, T2) => R` to a ParSeq `Function2[T1, T2, R]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam R The type parameter of the function and the ParSeq Function2
   * @return The ParSeq Function2
   */
  implicit def toFunction2[T1, T2, R](f: (T1, T2) => R): Function2[T1, T2, R] =
    new Function2[T1, T2, R] {
      override def apply(t1: T1, t2: T2): R = f(t1, t2)
    }

  /**
   * The method toFunction3 converts a function `(T1, T2, T3) => R` to a ParSeq `Function3[T1, T2, T3, R]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @tparam R The type parameter of the function and the ParSeq Function3
   * @return The ParSeq Function3
   */
  implicit def toFunction3[T1, T2, T3, R](f: (T1, T2, T3) => R): Function3[T1, T2, T3, R] =
    new Function3[T1, T2, T3, R] {
      override def apply(t1: T1, t2: T2, t3: T3): R = f(t1, t2, t3)
    }

  /**
   * The method toFunction4 converts a function `(T1, T2, T3, T4) => R` to a ParSeq `Function4[T1, T2, T3, T4, R]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @tparam T4 The type parameter of the fourth function parameter
   * @tparam R The type parameter of the function and the ParSeq Function4
   * @return The ParSeq Function4
   */
  implicit def toFunction4[T1, T2, T3, T4, R](f: (T1, T2, T3, T4) => R): Function4[T1, T2, T3, T4, R] =
    new Function4[T1, T2, T3, T4, R] {
      override def apply(t1: T1, t2: T2, t3: T3, t4: T4): R = f(t1, t2, t3, t4)
    }

  /**
   * The method toFunction5 converts a function `(T1, T2, T3, T4, T5) => R` to a ParSeq
   * `Function5[T1, T2, T3, T4, T5, R]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @tparam T4 The type parameter of the fourth function parameter
   * @tparam T5 The type parameter of the fifth function parameter
   * @tparam R The type parameter of the function and the ParSeq Function5
   * @return The ParSeq Function5
   */
  implicit def toFunction5[T1, T2, T3, T4, T5, R](f: (T1, T2, T3, T4, T5) => R): Function5[T1, T2, T3, T4, T5, R] =
    new Function5[T1, T2, T3, T4, T5, R] {
      override def apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5): R = f(t1, t2, t3, t4, t5)
    }

  /**
   * The method toFunction6 converts a function `(T1, T2, T3, T4, T5, T6) => R` to a ParSeq
   * `Function6[T1, T2, T3, T4, T5, T6, R]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @tparam T4 The type parameter of the fourth function parameter
   * @tparam T5 The type parameter of the fifth function parameter
   * @tparam T6 The type parameter of the sixth function parameter
   * @tparam R The type parameter of the function and the ParSeq Function6
   * @return The ParSeq Function6
   */
  implicit def toFunction6[T1, T2, T3, T4, T5, T6, R](f: (T1, T2, T3, T4, T5, T6) => R): Function6[T1, T2, T3, T4, T5, T6, R] =
    new Function6[T1, T2, T3, T4, T5, T6, R] {
      override def apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6): R = f(t1, t2, t3, t4, t5, t6)
    }

  /**
   * The method toFunction7 converts a function `(T1, T2, T3, T4, T5, T6, T7) => R` to a ParSeq
   * `Function7[T1, T2, T3, T4, T5, T6, T7, R]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @tparam T4 The type parameter of the fourth function parameter
   * @tparam T5 The type parameter of the fifth function parameter
   * @tparam T6 The type parameter of the sixth function parameter
   * @tparam T7 The type parameter of the seventh function parameter
   * @tparam R The type parameter of the function and the ParSeq Function7
   * @return The ParSeq Function7
   */
  implicit def toFunction7[T1, T2, T3, T4, T5, T6, T7, R](f: (T1, T2, T3, T4, T5, T6, T7) => R): Function7[T1, T2, T3, T4, T5, T6, T7, R] =
    new Function7[T1, T2, T3, T4, T5, T6, T7, R] {
      override def apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7): R = f(t1, t2, t3, t4, t5, t6, t7)
    }

  /**
   * The method toFunction8 converts a function `(T1, T2, T3, T4, T5, T6, T7, T8) => R` to a ParSeq
   * `Function8[T1, T2, T3, T4, T5, T6, T7, T8, R]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @tparam T4 The type parameter of the fourth function parameter
   * @tparam T5 The type parameter of the fifth function parameter
   * @tparam T6 The type parameter of the sixth function parameter
   * @tparam T7 The type parameter of the seventh function parameter
   * @tparam T8 The type parameter of the eighth function parameter
   * @tparam R The type parameter of the function and the ParSeq Function8
   * @return The ParSeq Function8
   */
  implicit def toFunction8[T1, T2, T3, T4, T5, T6, T7, T8, R](f: (T1, T2, T3, T4, T5, T6, T7, T8) => R): Function8[T1, T2, T3, T4, T5, T6, T7, T8, R] =
    new Function8[T1, T2, T3, T4, T5, T6, T7, T8, R] {
      override def apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8): R = f(t1, t2, t3, t4, t5, t6, t7, t8)
    }

  /**
   * The method toFunction9 converts a function `(T1, T2, T3, T4, T5, T6, T7, T8, T9) => R` to a ParSeq
   * `Function8[T1, T2, T3, T4, T5, T6, T7, T8, T9, R]`.
   *
   * @param f The function
   * @tparam T1 The type parameter of the first function parameter
   * @tparam T2 The type parameter of the second function parameter
   * @tparam T3 The type parameter of the third function parameter
   * @tparam T4 The type parameter of the fourth function parameter
   * @tparam T5 The type parameter of the fifth function parameter
   * @tparam T6 The type parameter of the sixth function parameter
   * @tparam T7 The type parameter of the seventh function parameter
   * @tparam T8 The type parameter of the eighth function parameter
   * @tparam T9 The type parameter of the ninth function parameter
   * @tparam R The type parameter of the function and the ParSeq Function9
   * @return The ParSeq Function9
   */
  implicit def toFunction9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => R): Function9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R] =
    new Function9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R] {
      override def apply(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9): R = f(t1, t2, t3, t4, t5, t6, t7, t8, t9)
    }

  /**
   * The method toTuple2 converts a Scala Tuple2 `(T1, T2)` to a ParSeq `Tuple2[T1, T2]`.
   *
   * @param t The Scala Tuple2
   * @tparam T1 The type parameter of the first tuple element
   * @tparam T2 The type parameter of the second tuple element
   * @return The ParSeq Tuple2
   */
  implicit def toTuple2[T1, T2](t: (T1, T2)): Tuple2[T1, T2] =
    new Tuple2[T1, T2](t._1, t._2)

  /**
   * The method toTuple3 converts a Scala Tuple3 `(T1, T2, T3)` to a ParSeq `Tuple3[T1, T2, T3]`.
   *
   * @param t The Scala Tuple3
   * @tparam T1 The type parameter of the first tuple element
   * @tparam T2 The type parameter of the second tuple element
   * @tparam T3 The type parameter of the third tuple element
   * @return The ParSeq Tuple3
   */
  implicit def toTuple3[T1, T2, T3](t: (T1, T2, T3)): Tuple3[T1, T2, T3] =
    new Tuple3[T1, T2, T3](t._1, t._2, t._3)

  /**
   * The method toTuple4 converts a Scala Tuple4 `(T1, T2, T3, T4)` to a ParSeq `Tuple4[T1, T2, T3, T4]`.
   *
   * @param t The Scala Tuple4
   * @tparam T1 The type parameter of the first tuple element
   * @tparam T2 The type parameter of the second tuple element
   * @tparam T3 The type parameter of the third tuple element
   * @tparam T4 The type parameter of the fourth tuple element
   * @return The ParSeq Tuple4
   */
  implicit def toTuple4[T1, T2, T3, T4](t: (T1, T2, T3, T4)): Tuple4[T1, T2, T3, T4] =
    new Tuple4[T1, T2, T3, T4](t._1, t._2, t._3, t._4)

  /**
   * The method toTuple5 converts a Scala Tuple5 `(T1, T2, T3, T4, T5)` to a ParSeq `Tuple5[T1, T2, T3, T4, T5]`.
   *
   * @param t The Scala Tuple5
   * @tparam T1 The type parameter of the first tuple element
   * @tparam T2 The type parameter of the second tuple element
   * @tparam T3 The type parameter of the third tuple element
   * @tparam T4 The type parameter of the fourth tuple element
   * @tparam T5 The type parameter of the fifth tuple element
   * @return The ParSeq Tuple5
   */
  implicit def toTuple5[T1, T2, T3, T4, T5](t: (T1, T2, T3, T4, T5)): Tuple5[T1, T2, T3, T4, T5] =
    new Tuple5[T1, T2, T3, T4, T5](t._1, t._2, t._3, t._4, t._5)

  /**
   * The method toTuple6 converts a Scala Tuple6 `(T1, T2, T3, T4, T5, T6)` to a ParSeq
   * `Tuple6[T1, T2, T3, T4, T5, T6]`.
   *
   * @param t The Scala Tuple6
   * @tparam T1 The type parameter of the first tuple element
   * @tparam T2 The type parameter of the second tuple element
   * @tparam T3 The type parameter of the third tuple element
   * @tparam T4 The type parameter of the fourth tuple element
   * @tparam T5 The type parameter of the fifth tuple element
   * @tparam T6 The type parameter of the sixth tuple element
   * @return The ParSeq Tuple6
   */
  implicit def toTuple6[T1, T2, T3, T4, T5, T6](t: (T1, T2, T3, T4, T5, T6)): Tuple6[T1, T2, T3, T4, T5, T6] =
    new Tuple6[T1, T2, T3, T4, T5, T6](t._1, t._2, t._3, t._4, t._5, t._6)

  /**
   * The method toTuple7 converts a Scala Tuple7 `(T1, T2, T3, T4, T5, T6, T7)` to a ParSeq
   * `Tuple7[T1, T2, T3, T4, T5, T6, T7]`.
   *
   * @param t The Scala Tuple7
   * @tparam T1 The type parameter of the first tuple element
   * @tparam T2 The type parameter of the second tuple element
   * @tparam T3 The type parameter of the third tuple element
   * @tparam T4 The type parameter of the fourth tuple element
   * @tparam T5 The type parameter of the fifth tuple element
   * @tparam T6 The type parameter of the sixth tuple element
   * @tparam T7 The type parameter of the seventh tuple element
   * @return The ParSeq Tuple7
   */
  implicit def toTuple7[T1, T2, T3, T4, T5, T6, T7](t: (T1, T2, T3, T4, T5, T6, T7)): Tuple7[T1, T2, T3, T4, T5, T6, T7] =
    new Tuple7[T1, T2, T3, T4, T5, T6, T7](t._1, t._2, t._3, t._4, t._5, t._6, t._7)

  /**
   * The method toTuple8 converts a Scala Tuple8 `(T1, T2, T3, T4, T5, T6, T7, T8)` to a ParSeq
   * `Tuple8[T1, T2, T3, T4, T5, T6, T7, T8]`.
   *
   * @param t The Scala Tuple8
   * @tparam T1 The type parameter of the first tuple element
   * @tparam T2 The type parameter of the second tuple element
   * @tparam T3 The type parameter of the third tuple element
   * @tparam T4 The type parameter of the fourth tuple element
   * @tparam T5 The type parameter of the fifth tuple element
   * @tparam T6 The type parameter of the sixth tuple element
   * @tparam T7 The type parameter of the seventh tuple element
   * @tparam T8 The type parameter of the eighth tuple element
   * @return The ParSeq Tuple8
   */
  implicit def toTuple8[T1, T2, T3, T4, T5, T6, T7, T8](t: (T1, T2, T3, T4, T5, T6, T7, T8)): Tuple8[T1, T2, T3, T4, T5, T6, T7, T8] =
    new Tuple8[T1, T2, T3, T4, T5, T6, T7, T8](t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)

  /**
   * The method toTuple9 converts a Scala Tuple9 `(T1, T2, T3, T4, T5, T6, T7, T8, T9)` to a ParSeq
   * `Tuple9[T1, T2, T3, T4, T5, T6, T7, T8, T9]`.
   *
   * @param t The Scala Tuple9
   * @tparam T1 The type parameter of the first tuple element
   * @tparam T2 The type parameter of the second tuple element
   * @tparam T3 The type parameter of the third tuple element
   * @tparam T4 The type parameter of the fourth tuple element
   * @tparam T5 The type parameter of the fifth tuple element
   * @tparam T6 The type parameter of the sixth tuple element
   * @tparam T7 The type parameter of the seventh tuple element
   * @tparam T8 The type parameter of the eighth tuple element
   * @tparam T9 The type parameter of the ninth tuple element
   * @return The ParSeq Tuple9
   */
  implicit def toTuple9[T1, T2, T3, T4, T5, T6, T7, T8, T9](t: (T1, T2, T3, T4, T5, T6, T7, T8, T9)): Tuple9[T1, T2, T3, T4, T5, T6, T7, T8, T9] =
    new Tuple9[T1, T2, T3, T4, T5, T6, T7, T8, T9](t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)

  /**
   * The method toAction converts a function `=> Unit` to a ParSeq `Action`.
   *
   * @param f The function
   * @return The ParSeq Action
   */
  implicit def toAction(f: => Unit): Action =
    new Action {
      override def run() = f
    }

  /**
   * The method toCallable converts a function `=> R` to a `Callable[R]`.
   *
   * @param f The function
   * @tparam R The type parameter of the function and the Callable
   * @return The Callable
   */
  implicit def toCallable[R](f: => R): Callable[R] =
    new Callable[R] {
      override def call(): R = f
    }

  /**
   * The method toConsumer converts a function `(T) => Any` to a `Consumer[T]`.
   *
   * @param f The function
   * @tparam T The type parameter of the function and the Consumer
   * @return The Consumer
   */
  implicit def toConsumer[T](f: (T) => Any): Consumer[T] =
    new Consumer[T] {
      override def accept(t: T) = f(t)
    }

  /**
   * The method toSupplier converts a function `=> R` to a `Supplier[R]`.
   *
   * @param f The function
   * @tparam R The type parameter of the function and the Supplier
   * @return The Supplier
   */
  implicit def toSupplier[R](f: => R): Supplier[R] =
    new Supplier[R] {
      override def get: R = f
    }
}
