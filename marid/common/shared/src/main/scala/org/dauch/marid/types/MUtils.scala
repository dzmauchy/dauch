package org.dauch.marid.types

private[types] object MUtils {

  implicit final class StringBuilderExtensions(private val b: StringBuilder) extends AnyVal {
    def addBounds(lowers: Vector[MType], uppers: Vector[MType]): StringBuilder = {
      if (lowers.nonEmpty) {
        b.append(lowers.mkString(" >: ", " & ", ""))
      }
      if (uppers.nonEmpty) {
        b.append(uppers.mkString(" <: ", " & ", ""))
      }
      b
    }
  }
}
