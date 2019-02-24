import sbt._

object Dependencies {
  private val akkaGroupId = "com.typesafe.akka"
  private val akkaVersion = "2.5.21"
  private val akkaHttpVersion = "10.1.7"
  lazy val `akka-actor`: ModuleID = akkaGroupId %% "akka-actor" % akkaVersion
  lazy val `akka-http`: ModuleID = akkaGroupId %% "akka-http" % akkaHttpVersion
  lazy val `akka-http-core`: ModuleID = akkaGroupId %% "akka-http-core" % akkaHttpVersion
  lazy val `akka-stream`: ModuleID = akkaGroupId %% "akka-stream" % akkaVersion
  lazy val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.5"
}
