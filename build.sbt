name := """proto"""

version := "1.0"

scalaVersion := "2.11.8"
val akkaVersion = "2.4.4"
val scalaTest = "2.2.6"
val sprayVersion = "1.3.3"
val json4SVersion = "3.3.0"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-core" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,

  "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,

  /*
  "org.json4s" %% "json4s-core" % json4SVersion,
  "org.json4s" %% "json4s-native" % json4SVersion,
  "org.json4s" %% "json4s-jackson" % json4SVersion,
    */

  "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % scalaTest % "test"
)

libraryDependencies += "com.lihaoyi" % "ammonite-repl" % "0.5.7" % "test" cross CrossVersion.full
initialCommands in (Test, console) := """ammonite.repl.Main.run("")"""


mainClass in(Compile, run) := Some("com.opalab.Main")

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.opalab"
)

enablePlugins(JavaAppPackaging)
enablePlugins(JavaServerAppPackaging)

mainClass in assembly := Some("com.opalab.Main")

test in assembly := {}

fork in run := true
connectInput in run := true

/*
* // Bash Script config
bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/app.conf""""
bashScriptExtraDefines += """addJava "-Dlog4j.configurationFile=${app_home}/../conf/log4j2.xml""""

// Bat Script config
batScriptExtraDefines += """set _JAVA_OPTS=%_JAVA_OPTS% -Dconfig.file=%AKKA_CLUSTER_NETSPLIT_SAMPLE_HOME%\\conf\\app.conf"""
batScriptExtraDefines += """set _JAVA_OPTS=%_JAVA_OPTS% -Dlog4j.configurationFile=%AKKA_CLUSTER_NETSPLIT_SAMPLE_HOME%\\conf\\log4j2.xml"""
Status API Training Shop Blog About

* */