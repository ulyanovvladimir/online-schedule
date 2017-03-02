import com.github.play2war.plugin._

name := """schedule"""

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.1"

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

libraryDependencies += "postgresql" % "postgresql" % "9.1-901-1.jdbc4"

libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "1.2.0"


herokuAppName in Compile := "schedule-imei-isu"