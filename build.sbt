ThisBuild / scalaVersion := "2.13.7"
ThisBuild / version := "0.1"
ThisBuild / organization := "dauch.org"

lazy val maridCommon = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("marid") / "common")
  .settings(
    name := "common",
    libraryDependencies := Seq(
      "com.typesafe.play" %%% "play-json" % "2.10.0-RC5"
    )
  )

lazy val dauch = project.in(file("."))
  .aggregate(maridCommon.js, maridCommon.jvm)