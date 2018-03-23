import scala.io.Source
import scala.language.postfixOps
import scala.util.Try

//Load version from the file so that Gradle/Shipkit and SBT use the same version
val playParSeqVersion = {
  val pattern = """^version=(.+)$""".r
  val source = Source.fromFile("version.properties")
  val version = Try(source.getLines.collectFirst {
    case pattern(v) => v
  }.get)
  source.close
  version.get
}

val playParSeqScalaVersion = "2.11.11"

val playParSeqCrossScalaVersions = Seq("2.11.11", "2.12.4")

val parSeqVersion = "2.6.22"

val commonsIoVersion = "2.5"

val mockitoVersion = "1.10.19"

lazy val commonSettings = List(
  version := playParSeqVersion,
  organization := "com.linkedin.play-parseq",
  scalaVersion := playParSeqScalaVersion,
  crossScalaVersions := playParSeqCrossScalaVersions
)

lazy val `play-parseq-root` =
  (project in file("."))
    .aggregate(
      `play-parseq`,
      `play-parseq-scala`,
      `play-parseq-trace`,
      `play-parseq-trace-scala`,
      `play-parseq-sample`
    )
    .settings(
      publishArtifact := false,
      commonSettings
    )

lazy val `play-parseq` =
  (project in file("src/core/java"))
    .enablePlugins(PlayJava)
    .settings(
      name := """play-parseq""",
      commonSettings,
      libraryDependencies ++= Seq(
        "org.mockito" % "mockito-core" % mockitoVersion % Test
      )
    )
    .dependsOn(
      `play-parseq-scala`
    )

lazy val `play-parseq-scala` =
  (project in file("src/core/scala"))
    .enablePlugins(PlayScala)
    .settings(
      name := """play-parseq-scala""",
      commonSettings,
      libraryDependencies ++= Seq(
        "com.linkedin.parseq" % "parseq" % parSeqVersion,
        specs2 % Test
      )
    )

lazy val `play-parseq-trace` =
  (project in file("src/trace/java"))
    .enablePlugins(PlayJava)
    .settings(
      name := """play-parseq-trace""",
      commonSettings,
      libraryDependencies ++= Seq(
        "org.mockito" % "mockito-core" % mockitoVersion % Test
      )
    )
    .dependsOn(
      `play-parseq` % "compile->compile;test->test",
      `play-parseq-trace-scala`
    )

lazy val `play-parseq-trace-scala` =
  (project in file("src/trace/scala"))
    .enablePlugins(PlayScala)
    .settings(
      name := """play-parseq-trace-scala""",
      commonSettings,
      libraryDependencies ++= Seq(
        "com.linkedin.parseq" % "parseq-tracevis" % parSeqVersion artifacts Artifact("parseq-tracevis", "tar.gz", "tar.gz"),
        "com.linkedin.parseq" % "parseq-tracevis-server" % parSeqVersion,
        "commons-io" % "commons-io" % commonsIoVersion,
        specs2 % Test
      ),
      routesGenerator := InjectedRoutesGenerator,
      update in Compile := {
        val updateReport = (update in Compile).value
        val classDir = (classDirectory in Compile).value
        if (!(classDir / "tracevis").exists) {
          streams.value.log.info(s"Extracting parseq-tracevis to $classDir")
          updateReport.select(artifact = artifactFilter(name = "parseq-tracevis", extension = "tar.gz")).foreach(
            tarGz => s"mkdir -p $classDir" #&& s"tar -zxf ${tarGz.getAbsolutePath} -C $classDir" !
          )
        }
        updateReport
      }
    )
    .dependsOn(
      `play-parseq-scala`
    )

lazy val `play-parseq-sample` =
  (project in file("sample"))
    .enablePlugins(PlayJava, PlayScala)
    .settings(
      name := """play-parseq-sample""",
      commonSettings,
      libraryDependencies ++= Seq(
        guice,
        javaWs,
        ws,
        "com.linkedin.parseq" % "parseq-http-client" % parSeqVersion,
        specs2 % Test
      ),
      publishArtifact := false
    )
    .dependsOn(
      `play-parseq-trace`
    )
