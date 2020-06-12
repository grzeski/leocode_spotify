import sbt.Keys._
import sbt._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport._


object CommonSettings {

  val additionalScalacOptions = Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-unchecked",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Xfuture",
    "-Ywarn-unused-import",
    "-Ypartial-unification",
    "-Xfatal-warnings"
  )


  val commonProjectSettings = Seq(
    scalaVersion := "2.12.8",
    organization := "com.leocode",
    scalacOptions ++= additionalScalacOptions,
    scalafmtOnCompile := true,
    scalacOptions in (Compile, console) --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings"),
  )


}
