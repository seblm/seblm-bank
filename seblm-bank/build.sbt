import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "name.lemerdy.sebastian",
      scalaVersion := "2.12.8",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "seblm-bank",
    libraryDependencies += `akka-actor`,
    libraryDependencies += `akka-http`,
    libraryDependencies += `akka-http-core`,
    libraryDependencies += `akka-stream`,
    libraryDependencies += scalaTest % Test
  ).
  enablePlugins(SbtTwirl)
