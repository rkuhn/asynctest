name := "asynctest"

organization := "com.rolandkuhn"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-async" % "0.9.2",
  "junit" % "junit" % "4.11"
)

scalaVersion := "2.11.2"
