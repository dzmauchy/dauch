package org.dauch.util

import org.dauch.lifecycle.Stop

final class Closer private() {

  private final var stops = List.empty[(AnyRef, Stop[AnyRef])]

  private def obtain[T <: AnyRef](s: => T): T = try s catch {
    case e: Throwable =>
      throw e
  }

  private def add[T <: AnyRef : Stop](s: T): Unit = {
    val ss = s.asInstanceOf[AnyRef]
    val st = summon[Stop[T]].asInstanceOf[Stop[AnyRef]]
    val h = ss -> st
    synchronized {
      stops = h :: stops
    }
  }

  def apply[T <: AnyRef : Stop](s: => T): T = {val ss = obtain(s); add(ss); ss }
  def use[T <: AnyRef : Stop](s: => T): Unit = add(obtain(s))

  private def close(error: Throwable): Unit = {

  }
}

object Closer {
  def apply[R](f: Closer => R): R = {
    val c = new Closer
    val r = try {
      f(c)
    } catch {
      case x: Throwable => c.close(x); throw x
    }
    c.close(null)
    r
  }
}
