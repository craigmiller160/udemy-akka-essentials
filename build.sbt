ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val scalazVersion = "7.3.6"
val akkaVersion = "2.6.18"
val scalaTestVersion = "3.2.11"
val catsVersion = "2.7.0"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion,
  "org.typelevel" %% "cats-core" % catsVersion
)

lazy val root = (project in file("."))
  .settings(
    name := "udemy-akka-essentials",
    idePackagePrefix := Some("io.craigmiller160.akka")
  )
