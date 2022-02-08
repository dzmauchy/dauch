import sbt.Keys._
import sbt._

object BuildPlugin extends AutoPlugin {

  object autoImport {
  }

  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalacOptions := Seq(
      "-no-indent",
      "--deprecation",
      "--release:17",
      "-old-syntax",
      "-Werror"
    )
  )
}