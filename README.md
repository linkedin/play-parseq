# Play-ParSeq

[![Build Status](https://travis-ci.org/linkedin/play-parseq.svg?branch=master)](https://travis-ci.org/linkedin/play-parseq)

Play-ParSeq is a Play module which seamlessly integrates [ParSeq](https://github.com/linkedin/parseq) with Play Framework.

ParSeq is a Java framework for writing async code, which has several advantages over Java CompletionStage or Scala Future, e.g. ParSeq Trace for async code's runtime visualization, async code reuse via Task composition and taking control of async code's lifecycle.

Key features:

* Executes ParSeq `Task` and generates Java `CompletionStage` or Scala `Future`, which allows ParSeq Tasks to be used for executing Play Action.
* Converts from Java `Callable<CompletionStage<T>>` or Scala `() => Future[T]` to ParSeq `Task`, which allows existing code using Play native APIs to be integrated with ParSeq Tasks.
* Supports [ParSeq Trace](https://github.com/linkedin/parseq/wiki/Tracing).
* Provides both Scala and Java API.
* Requires Play 2.6.

## Releasing

Every change on master branch, for example a merged pull request, lands a new version in
[Bintray's JCenter](https://bintray.com/linkedin/maven/play-parseq)
and [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3Acom.linkedin.play-parseq).
This way we continuously deliver improvements in small batches, ensuring quality and compatibility.
Check out [latest release notes](https://github.com/linkedin/play-parseq/blob/master/docs/release-notes.md)!
Release automation is handled by Shipkit (http://shipkit.org)
and configured in [shipkit.gradle](https://github.com/linkedin/play-parseq/blob/master/gradle/shipkit.gradle).

## Quick start

### Core Java

1. Put the preset module `PlayParSeqModule` into your `application.conf`.

    ```
    ...
    play.modules.enabled += "com.linkedin.playparseq.j.modules.PlayParSeqModule"
    ...
    ```

2. Inject `PlayParSeq` into your Controller.

    ```java
    ...
    private final PlayParSeq _playParSeq;
    @Inject
    public Sample(final PlayParSeq playParSeq) {
        _playParSeq = playParSeq;
    ...
    ```

3. Use `PlayParSeq` in your Action.

    ```java
    ...
    public CompletionStage<Result> demo() {
        // Convert to ParSeq Task
        Task<String> helloworldTask = _playParSeq.toTask("helloworld", () -> CompletableFuture.completedFuture("Hello World"));
        // Run the Task
        return _playParSeq.runTask(Http.Context.current(), helloworldTask).thenApply(Results::ok);
    }
    ...
    ```

#### Enable ParSeq Trace Java

1. Put additional preset module for `ParSeqTraceModule` into your `application.conf`.

    ```
    ...
    play.modules.enabled += "com.linkedin.playparseq.trace.j.modules.ParSeqTraceModule"
    ...
    ```

2. Put ParSeq Trace resource route into your `routes`.

    ```
    ...
    ->         /                       com.linkedin.playparseq.trace.Routes
    ...
    ```

3. Annotate your Action by putting `@With(ParSeqTraceAction.class)`.

    ```java
    ...
    @With(ParSeqTraceAction.class)
    public CompletionStage<Result> demo() {
    ...
    ```

4. Access `[original-route]?parseq-trace=true` will display ParSeq Trace Viewer for your original request if your application is in `dev` mode.

### Core Scala

1. Put the preset module `PlayParSeqModule` into your `application.conf`.

    ```
    ...
    play.modules.enabled += "com.linkedin.playparseq.s.modules.PlayParSeqModule"
    ...
    ```

2. Inject `PlayParSeq` into your Controller.

    ```scala
    ...
    class Sample @Inject()(playParSeq: PlayParSeq, cc: ControllerComponents) extends AbstractController(cc) {
    ...
    ```

3. Use `PlayParSeq` in your Action.

    ```scala
    ...
    def demo = Action.async(implicit request => {
        // Convert to ParSeq Task
        val helloworldTask = playParSeq.toTask("helloworld", () => Future("Hello World"))
        // Run the Task
        playParSeq.runTask(helloworldTask)
            .map(Ok(_))
    })
    ...
    ```

#### Enable ParSeq Trace Scala

1. Put additional preset module for `ParSeqTraceModule` into your `application.conf`.

    ```
    ...
    play.modules.enabled += "com.linkedin.playparseq.trace.s.modules.ParSeqTraceModule"
    ...
    ```

2. Put ParSeq Trace resource route into your `routes`.

    ```
    ...
    ->         /                       com.linkedin.playparseq.trace.Routes
    ...
    ```

3. Inject `ParSeqTraceAction` into your Controller.

    ```scala
    ...
    class Sample @Inject()(playParSeq: PlayParSeq, parSeqTraceAction: ParSeqTraceAction, cc: ControllerComponents) extends AbstractController(cc) {
    ...
    ```

4. Use `parSeqTraceAction.async` for your Action.

    ```scala
    ...
    def demo = parSeqTraceAction.async(implicit request => {
    ...
    ```

5. Access `[original-route]?parseq-trace=true` will display ParSeq Trace Viewer for your original request if your application is in `dev` mode.

### More examples

Please see `/sample`.

## FAQ

### Why is there no ParSeq Trace Viewer even though I've added `parseq-trace=true` to my query?

**A:** Please first make sure you annotated your Action with `@With(ParSeqTraceAction.class)` in Java, or you used `parSeqTraceAction.async` for your Action in Scala. Then check whether you meet all `ParSeqTraceSensor` requirements of showing ParSeq Trace. And also don't forget using `runTask` in your Action. Please also note that ParSeq Trace Viewer will be blocked by strict Content Security Policy rules because of some inline scripts and styles.

### How can I use my own module settings?

**A:** If you don't want to use the preset modules, please write your own Module and register it by putting into your `application.conf`. Almost every part of Play-ParSeq follows the DI, so you can easily replace any part with your own.

### Are the preset modules configurable?

**A:** Yes. If you don't want to use the default value, please insert the corresponding settings into your `application.conf`.

| Name | Description | Default |
| --- | --- | --- |
| parseq.engine.numThreads | The number of threads in Engine's pool. | Available processors + 1 |
| parseq.engine.terminationWaitSeconds | The maximum time to wait for Engine's termination in the unit of seconds. | 1 |
| parseq.trace.docLocation | The file path of the dot, which is part of [graphviz](http://www.graphviz.org/) for generating Task's graphviz view. | Registered location if installed |
| parseq.trace.cacheSize | The number of cache items in GraphvizEngine. | 1024 |
| parseq.trace.getTimeoutMilliseconds | The timeout of the GraphvizEngine execution in the unit of milliseconds. | 5000 |
| parseq.trace.parallelLevel | The maximum of the GraphvizEngine's parallel level. | 1 |
| parseq.trace.delayMilliseconds | The delay time between different executions of the GraphvizEngine in the unit of milliseconds. | 5 |
| parseq.trace.processQueueSize | The size of the GraphvizEngine's process queue. | 1000 |

### Can I run multiple ParSeq Tasks in one request?

**A:** Yes. Play-ParSeq supports this. **However**, you shouldn't be running multiple Tasks, otherwise the order of execution might not be accurate, which minimizes the benefits of ParSeq.

### Does ParSeq Trace support streaming?

**A:** Yes.

### How can I replace the requirements of showing ParSeq Trace?

**A:** You can follow the instructions below:

1. Implements `ParSeqTraceSensor` with your own requirements.

    ```java
    ...
    // Java
    @Singleton
    public class MySensorImpl implements ParSeqTraceSensor {
        @Override
        public boolean isEnabled(final Http.Context context, final ParSeqTaskStore parSeqTaskStore) {
            return [your-requirements];
        }
    }
    ...
    ```

    ```scala
    ...
    // Scala
    @Singleton
    class MySensorImpl extends ParSeqTraceSensor {
        override def isEnabled(requestHeader: RequestHeader, parSeqTaskStore: ParSeqTaskStore): Boolean =
            [your-requirements]
    }
    ...
    ```

2. Create your own Module to include the binding of your `ParSeqTraceSensor`.

    ```java
    ...
    // Java
    public class MyModule extends Module {
        @Override
        public Seq<Binding<?>> bindings(final Environment environment, final Configuration configuration) {
            return seq(
                bind(ParSeqTraceSensor.class).to(MySensorImpl.class),
                [other-bindings]);
        }
    }
    ...
    ```

    ```scala
    ...
    // Scala
    class MyModule extends Module {
        override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
            bind[ParSeqTraceSensor].to[MySensorImpl],
            [other-bindings])
    }
    ...
    ```

3. Register your Module in your `application.conf`.

    ```
    ...
    play.modules.enabled += [package-of-MyModule]
    ...
    ```

4. Have two ice-creams.

### Can I put in the Java version of ParSeqTaskStore and then get from the Scala version?

**A:** It's not possible with the current ParSeqTaskStore implementation, which is based on Play Framework's Java or Scala specific request APIs.
Play 3.0 will hopefully provide a common underlying request which will remove this limitation.
However, you can inject your own implementation of ParSeqTaskStore, such as shared cache or local file, to make this happen.

## License

Copyright 2015 LinkedIn Corp.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
