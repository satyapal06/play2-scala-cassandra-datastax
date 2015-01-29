name := """play2-scala-cassandra-datastax"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" %	"bootstrap" % "3.1.1",
  "org.webjars" % "bootswatch-united"	% "3.1.1",
  "org.webjars" % "html5shiv" % "3.7.2",
  "org.webjars" % "respond" % "1.4.2",
  "org.webjars" % "font-awesome" % "4.3.0-1",
  "com.datastax.cassandra"  % "cassandra-driver-core" % "2.1.4"
)
