import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "name.lemerdy.sebastian",
      scalaVersion := "2.12.2",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "seblm-bank",
    libraryDependencies += `akka-http`,
    libraryDependencies += `akka-http-core`,
    libraryDependencies += scalaTest % Test
  )
