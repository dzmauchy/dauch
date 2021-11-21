package org.dauch.marid.types

import org.dauch.marid.types.MClass.Var
import org.dauch.marid.types.MUtils.*

import scala.annotation.tailrec
import scala.math.Ordering

abstract sealed class MClass {
  def name: String
  def vars: Vector[Var]
  override def toString: String = name
}

object MClass {

  final case object Any extends MClass {
    override def name: String = "Any"
    override def vars: Vector[Var] = Vector.empty
  }
  final case object Nothing extends MClass {
    override def name: String = "Nothing"
    override def vars: Vector[Var] = Vector.empty
  }
  final case class Simple(name: String) extends MClass {
    override def vars: Vector[Var] = Vector.empty
  }
  final case class Parameterized(name: String, vars: Vector[Var])

  abstract sealed class Variance
  final case object Covariant extends Variance {
    override def toString: String = "+"
  }
  final case object Contravariant extends Variance {
    override def toString: String = "-"
  }
  final case object Invariant extends Variance {
    override def toString: String = ""
  }

  final case class Var(variance: Variance, name: String, lowers: Vector[MType], uppers: Vector[MType]) {
    override def toString: String = new StringBuilder(s"$variance$name").addBounds(lowers, uppers).toString()
  }

  object Var {
    implicit final class VarExtensions(private val v: Var) extends AnyVal {
      def covariant: Boolean = v.variance eq Covariant
      def contravariant: Boolean = v.variance eq Contravariant
      def invariant: Boolean = v.variance eq Invariant
    }
  }

  final case class Desc private(c: MClass, supertypes: Vector[MType], funcs: Vector[MFunc]) {

    def func(name: String): Option[MFunc] = binarySearch(name, 0, funcs.length)

    @tailrec
    private def binarySearch(elem: String, from: Int, to: Int): Option[MFunc] = {
      if (to <= from) None
      else {
        val idx = from + (to - from - 1) / 2
        val e = funcs(idx)
        val c = elem.compareTo(e.name)
        if (c == 0) Some(e)
        else if (c < 0) binarySearch(elem, from, idx)
        else binarySearch(elem, idx + 1, to)
      }
    }
  }
  object Desc {
    def apply(c: MClass, supertypes: Vector[MType], funcs: MFunc*): Desc = {
      Desc(c, supertypes, Vector.from(funcs.sorted(Ordering.by((e: MFunc) => e.name))))
    }
  }
}