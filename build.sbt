import sbt._
import Keys._
import CommonSettings._

lazy val akkaVer = "2.5.26"
val sttpVersion = "2.1.5"

val dependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVer,
  "com.typesafe.akka" %% "akka-stream" % akkaVer,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVer,
  "com.softwaremill.sttp.client" %% "core" % sttpVersion,
  "com.softwaremill.sttp.client" %% "play-json" % sttpVersion,
  "com.softwaremill.sttp.client" %% "akka-http-backend" % sttpVersion,
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  "com.typesafe.play" %% "play-logback" % "2.6.1",
  "is.cir" %% "ciris-cats" % "0.9.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
)

val dependenciesTest = Seq(
  "org.mockito" % "mockito-core" % "2.7.6" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
)


lazy val root = (project in file("."))
  .settings(
    name := "leocode",
    version := "0.0.0"
  )
  .settings(commonProjectSettings)
  .enablePlugins(JavaAppPackaging)
  .settings(autoCompilerPlugins := true)
  .settings(addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.1" cross CrossVersion.full))
  .settings(libraryDependencies ++= dependencies ++ dependenciesTest)
