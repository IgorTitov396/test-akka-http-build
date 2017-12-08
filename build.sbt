lazy val mainSettings = Seq(
  version := "0.0.1",
  organization := "com.akit-development-team",
  scalaVersion := "2.12.4"
)

lazy val dependenies = libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-http" % "10.0.11",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.11" % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "com.iheart" %% "ficus" % "1.4.3"
)

lazy val testClient = (project in file("client"))
  .settings(
    name := "test-akka-http-client",
    mainSettings,
    dependenies
  )

lazy val testServer = (project in file("server"))
  .settings(
    name := "test-akka-http-server",
    mainSettings,
    dependenies
  )
