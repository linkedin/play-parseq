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
package com.linkedin.playparseq.j;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.Task;
import com.linkedin.playparseq.j.stores.ParSeqTaskStore;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.libs.F;
import play.mvc.Http;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


/**
 * The class PlayParSeqImplTest is a test class for {@link PlayParSeqImpl}.
 *
 * @author Yinan Ding (yding@linkedin.com)
 */
public class PlayParSeqImplTest {

  /**
   * The field _playParSeqImpl is the {@link PlayParSeqImpl} to be tested.
   */
  private PlayParSeqImpl _playParSeqImpl;

  /**
   * The field _engine is a ParSeq Engine for running ParSeq Task.
   */
  private Engine _engine;

  /**
   * The field _taskScheduler is a task scheduler for ParSeq Engine.
   */
  private ExecutorService _taskScheduler;

  /**
   * The field _timerScheduler is a timer scheduler for ParSeq Engine.
   */
  private ScheduledExecutorService _timerScheduler;

  /**
   * The filed _mockContext is mocking Http.Context.
   */
  private Http.Context _mockContext;

  /**
   * The field DEFAULT_TIME_OUT is the default time out value for retrieving data from Play Promise in the unit of ms.
   */
  public final static int DEFAULT_TIME_OUT = 5000;

  /**
   * The method setUp sets the ParSeq Engine and the {@link PlayParSeqImpl}.
   *
   * @see <a href="https://github.com/linkedin/parseq/wiki/User's-Guide#creating-an-engine">ParSeq Wiki</a>
   */
  @Before
  public void setUp() {
    _taskScheduler = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    _timerScheduler = Executors.newSingleThreadScheduledExecutor();
    _engine = new EngineBuilder().setTaskExecutor(_taskScheduler).setTimerScheduler(_timerScheduler).build();
    _playParSeqImpl = new PlayParSeqImpl(_engine, mock(ParSeqTaskStore.class));
    _mockContext = mock(Http.Context.class);
  }

  /**
   * The method tearDown tears the ParSeq Engine.
   *
   * @see <a href="https://github.com/linkedin/parseq/wiki/User's-Guide#creating-an-_engine">ParSeq Wiki</a>
   * @throws InterruptedException The exception from awaitTermination
   */
  @After
  public void tearDown()
      throws InterruptedException {
    _engine.shutdown();
    _engine.awaitTermination(1, TimeUnit.SECONDS);
    _taskScheduler.shutdown();
    _timerScheduler.shutdown();
  }

  /**
   * The method canConvertToTaskWithGivenName tests the ability of converting to a ParSeq Task with given name.
   */
  @Test
  public void canConvertToTaskWithGivenName() {
    String name = "pure";
    // Convert
    Task<String> task = _playParSeqImpl.toTask(name, () -> F.Promise.pure("Test"));
    // Assert the name
    assertEquals(name, task.getName());
  }

  /**
   * The method canConvertToTaskWithDefaultName tests the ability of converting to a ParSeq Task with default name.
   */
  @Test
  public void canConvertToTaskWithDefaultName() {
    // Convert
    Task<String> task = _playParSeqImpl.toTask(() -> F.Promise.pure("Test"));
    // Assert the name
    assertEquals(PlayParSeqImpl.DEFAULT_TASK_NAME, task.getName());
  }

  /**
   * The method canConvertToTaskWithSuccess tests the ability of converting to a ParSeq Task which can succeed.
   */
  @Test
  public void canConvertToTaskWithSuccess() {
    String testString = "Test";
    int start = testString.length() - 1;
    // Convert then run
    F.Promise<String> playPromise = _playParSeqImpl
        .runTask(_mockContext,
            _playParSeqImpl.toTask("substring", () -> F.Promise.promise(() -> testString.substring(start))));
    // Assert the result from the Play Promise
    assertEquals(testString.substring(start), playPromise.get(DEFAULT_TIME_OUT));
  }

  /**
   * The method canConvertToTaskWithRecover tests the ability of converting to a ParSeq Task which can recover.
   */
  @Test
  public void canConvertToTaskWithRecover() {
    String testString = "Test";
    String recoverString = "Recover";
    // With an invalid substring start index
    int start = testString.length() + 1;
    // Convert then run then recover
    F.Promise<String> playPromise = _playParSeqImpl
        .runTask(_mockContext,
            _playParSeqImpl.toTask("substring", () -> F.Promise.promise(() -> testString.substring(start))))
            .recover(throwable -> recoverString);
    // Assert the result from the Play Promise which should be the recover value
    assertEquals(recoverString, playPromise.get(DEFAULT_TIME_OUT));
  }

  /**
   * The method canConvertToTaskWithFailure tests the ability of converting to a ParSeq Task which can fail.
   */
  @Test(expected = StringIndexOutOfBoundsException.class)
  public void canConvertToTaskWithFailure() {
    String testString = "Test";
    // With an invalid substring start index
    int start = testString.length() + 1;
    // Convert then run
    F.Promise<String> playPromise = _playParSeqImpl
        .runTask(_mockContext,
            _playParSeqImpl.toTask("substring", () -> F.Promise.promise(() -> testString.substring(start))));
    // Get value from the Play Promise to trigger the exception
    playPromise.get(DEFAULT_TIME_OUT);
  }

  /**
   * The method canRunTaskWithSuccess tests the ability of running a ParSeq Task which can succeed.
   */
  @Test
  public void canRunTaskWithSuccess() {
    String testString = "Test";
    int start = testString.length() - 1;
    // Run
    F.Promise<String> playPromise = _playParSeqImpl.runTask(_mockContext,
        Task.callable("test", () -> testString.substring(start)));
    // Assert the result from the Play Promise
    assertEquals(testString.substring(start), playPromise.get(DEFAULT_TIME_OUT));
  }

  /**
   * The method canRunTaskWithRecover tests the ability of running a ParSeq Task which can recover.
   */
  @Test
  public void canRunTaskWithRecover() {
    String testString = "Test";
    String recoverString = "Recover";
    // With an invalid substring start index
    int start = testString.length() + 1;
    // Run then recover
    F.Promise<String> playPromise = _playParSeqImpl.runTask(_mockContext,
        Task.callable("test", () -> testString.substring(start))).recover(throwable -> recoverString);
    // Assert the result from the Play Promise which should be the recover value
    assertEquals(recoverString, playPromise.get(DEFAULT_TIME_OUT));
  }

  /**
   * The method canRunTaskWithFailure tests the ability of running a ParSeq Task which can fail.
   */
  @Test(expected = StringIndexOutOfBoundsException.class)
  public void canRunTaskWithFailure() {
    String testString = "Test";
    // With an invalid substring start index
    int start = testString.length() + 1;
    // Run
    F.Promise<String> playPromise = _playParSeqImpl.runTask(_mockContext,
        Task.callable("test", () -> testString.substring(start)));
    // Get value from the Play Promise to trigger the exception
    playPromise.get(DEFAULT_TIME_OUT);
  }
}
