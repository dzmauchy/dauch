import sbt.Keys._
import sbt._

object BuildPlugin extends AutoPlugin {

  object autoImport {
  }

  override def trigger = allRequirements

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalacOptions := Seq(
      "-target:16",
      "-Xsource:3",
      "-opt:l:method"
    )
  )
}