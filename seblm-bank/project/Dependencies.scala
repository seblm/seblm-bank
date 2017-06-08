import sbt._

object Dependencies {
  lazy val `akka-http`: ModuleID = "com.typesafe.akka" %% "akka-http" % "10.0.7"
  lazy val `akka-http-core`: ModuleID = "com.typesafe.akka" %% "akka-http-core" % "10.0.7"
  lazy val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.1"
}
