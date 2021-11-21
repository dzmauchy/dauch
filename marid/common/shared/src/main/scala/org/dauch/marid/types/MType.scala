package org.dauch.marid.types

import org.dauch.marid.types.MType.VarRef
import org.dauch.marid.types.MUtils.*

sealed abstract class MType {
  def ground: Boolean
  def nonGround: Boolean
  def contains(ref: VarRef): Boolean
}
object MType {
  final case class Simple(c: MClass) extends MType {
    override def ground: Boolean = true
    override def nonGround: Boolean = false
    override def contains(ref: VarRef): Boolean = false
    override def toString: String = c.toString
  }
  final case class Parameterized(c: MClass, args: MType*) extends MType {
    override def ground: Boolean = args.forall(_.ground)
    override def nonGround: Boolean = args.exists(_.nonGround)
    override def contains(ref: VarRef): Boolean = args.exists(_.contains(ref))
    override def toString: String = args.mkString(s"$c[", " & ", "]")
  }
  final case class Intersection(types: MType*) extends MType {
    override def ground: Boolean = types.forall(_.ground)
    override def nonGround: Boolean = types.exists(_.nonGround)
    override def contains(ref: VarRef): Boolean = types.exists(_.contains(ref))
    override def toString: String = types.mkString(" & ")
  }
  final case class Wild(lowers: Vector[MType], uppers: Vector[MType]) extends MType {
    override def ground: Boolean = lowers.forall(_.ground) && uppers.forall(_.ground)
    override def nonGround: Boolean = lowers.exists(_.nonGround) || uppers.exists(_.nonGround)
    override def contains(ref: VarRef): Boolean = lowers.exists(_.contains(ref)) || uppers.exists(_.contains(ref))
    override def toString: String = new StringBuilder("?").addBounds(lowers, uppers).toString()
  }
  abstract sealed class VarRef extends MType {
    def cName: String
    def name: String
    final override def contains(ref: VarRef): Boolean = ref == this
  }
  final case class CVarRef(cName: String, name: String) extends VarRef {
    override def ground: Boolean = false
    override def nonGround: Boolean = true
    override def toString: String = s"$cName.$name"
  }
  final case class FVarRef(cName: String, fName: String, name: String) extends VarRef {
    override def ground: Boolean = false
    override def nonGround: Boolean = true
    override def toString: String = s"$cName.$fName.$name"
  }

  abstract sealed class ResolveResult
  object ResolveResult {
    final case class Success(t: MType) extends ResolveResult
    abstract sealed class Failure extends ResolveResult {
      def toException: RuntimeException
      def source: String
      def func: String
    }
    final case class Error(source: String, func: String, message: String) extends Failure {
      override def toException: RuntimeException = new ResolveException(s"$message in $source.$func")
    }
    final case class MultipleErrors(source: String, func: String, errors: (String, String)*) extends Failure {
      override def toException: RuntimeException = {
        val e = new ResolveException(s"Resolve error in $source.$func")
        errors.foreach { case (arg, error) =>
          e.addSuppressed(new ResolveException(s"$error in $arg"))
        }
        e
      }
    }
    final class ResolveException(message: String) extends RuntimeException(message, null, true, false)
  }
}
