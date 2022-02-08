import Dependencies._
import sbt.Keys._
import sbt._

object BuildPlugin extends AutoPlugin {

  object autoImport {
  }

  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalacOptions ++= Seq(
      "-no-indent",
      "--deprecation",
      "--release:17",
      "-old-syntax",
      "-Werror",

    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalatestVersion % Test,
      "org.scalatest" %% "scalatest-wordspec" % scalatestVersion % Test,
      "org.scalatest" %% "scalatest-shouldmatchers" % scalatestVersion % Test
    )
  )
}