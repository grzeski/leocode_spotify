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
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "io.scarman" %% "spotify-api" % "0.2.0",
  "org.typelevel" %% "cats-effect" % "2.1.2",
  "is.cir" %% "ciris" % "1.0.4"
)

val flywayVersion = "6.3.2"
val doobieVersion = "0.8.8"
val testcontainersScalaVersion = "0.36.1"
val db = Seq(
  "org.flywaydb" % "flyway-core" % flywayVersion,
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-quill" % doobieVersion,
  "org.tpolecat" %% "doobie-scalatest" % doobieVersion % "test",
  "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersScalaVersion % "test",
  "com.dimafeng" %% "testcontainers-scala-postgresql" % testcontainersScalaVersion % "test"
)

val dependenciesTest = Seq(
  "org.mockito" % "mockito-core" % "2.7.6" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
)

resolvers ++= Seq(Resolver.JCenterRepository, Resolver.sonatypeRepo("releases"), Resolver.sonatypeRepo("snapshots"))

lazy val root = (project in file("."))
  .settings(
    name := "leocode",
    version := "0.0.0"
  )
  .settings(commonProjectSettings)
  .enablePlugins(JavaAppPackaging)
  .settings(autoCompilerPlugins := true)
  .settings(addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.1" cross CrossVersion.full))
  .settings(libraryDependencies ++= dependencies ++ db ++ dependenciesTest)
