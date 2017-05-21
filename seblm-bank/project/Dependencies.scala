import sbt._

object Dependencies {
  lazy val `akka-actor`: ModuleID = "com.typesafe.akka" %% "akka-actor" % "2.4.17"
  lazy val `akka-stream`: ModuleID = "com.typesafe.akka" %% "akka-stream" % "2.4.17"
  lazy val `akka-http`: ModuleID = "com.typesafe.akka" %% "akka-http" % "10.0.6"
  lazy val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.1"
}
