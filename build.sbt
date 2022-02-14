ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val scalazVersion = "7.3.6"
val akkaVersion = "2.6.18"
val scalaTestVersion = "3.211"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion
)

lazy val root = (project in file("."))
  .settings(
    name := "udemy-akka-essentials",
    idePackagePrefix := Some("io.craigmiller160.akka")
  )
