name := """proto"""

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

resolvers += "spray repo" at "http://repo.spray.io"

// javaOptions += "-Xms2G -Xmx2G"

libraryDependencies ++= {
  val akkaVersion = "2.4.4"
  val scalaTest = "2.2.6"
  val sprayVersion = "1.3.3"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,


    // "io.spray" %% "spray-can" % sprayVersion,
    // "io.spray" %% "spray-routing" % sprayVersion,
    // "io.spray" %% "spray-util" % sprayVersion,
    // "io.spray" %% "spray-testkit" % sprayVersion % "test",


    "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,

    "com.typesafe.akka" %% "akka-http-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTest % "test"
  )
}

// Revolver.settings

// mainClass in (Compile, run) := Some("com.opalab.ProtoMain")

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.opalab"
)

/*
lazy val app = (project in file("app")).
  settings(commonSettings: _*).
  settings(
    // your settings here
  )
*/

enablePlugins(JavaAppPackaging)
enablePlugins(JavaServerAppPackaging)

mainClass in assembly := Some("com.opalab.Main")

test in assembly := {}


// "org.scalatest" %% "scalatest" % "2.1.6" % "test",
// "com.typesafe.akka" %% "akka-actor" % "2.4.4"
//"com.typesafe.akka" %% "akka-remote" % "2.3.5",
//"com.typesafe.akka" %% "akka-testkit" % "2.3.5"

fork in run := true
connectInput in run := true