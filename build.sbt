name := """schedule"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "com.googlecode.json-simple" % "json-simple" % "1.1.1",
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)

libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "1.2.0"