package org.dauch.marid.types

import org.dauch.marid.types.MFunc.Var
import org.dauch.marid.types.MUtils.*

case class MFunc(
  c: MClass,
  name: String,
  static: Boolean,
  vars: Seq[Var],
  result: MType,
  args: (String, MType)*
) {
  override def toString: String = {
    val b = new StringBuilder(c.name).addOne('.')
    if (static) {
      b.addOne('*')
    }
    b.append(name)
    if (vars.nonEmpty) {
      b.append(vars.mkString("[", ",", "]"))
    }
    b
      .append(args.mkString("(", ",", "):"))
      .append(result)
      .toString()
  }
}
object MFunc {
  final case class Var(name: String, lowers: Vector[MType], uppers: Vector[MType]) {
    override def toString: String = new StringBuilder(name).addBounds(lowers, uppers).toString()
  }
}
