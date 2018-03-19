import Dependencies._
lazy val akkaVersion = "2.5.3"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.marklenoury",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "shaka",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-remote" % akkaVersion,
      "com.github.scopt" % "scopt_2.12" % "3.7.0"
    )
  )

