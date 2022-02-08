val scalaVer = "3.1.1"

ThisBuild / scalaVersion := scalaVer
ThisBuild / version := "0.1"
ThisBuild / organization := "dauch.org"

import Dependencies._

lazy val ide = project
  .in(file("ide"))
  .settings(
    name := "ide",
    libraryDependencies ++= Seq(
      "org.scala-lang" %% "scala3-compiler" % scalaVer,
      "org.openjfx" % "javafx-controls" % javafxVersion
    )
  )