package org.dauch.util

import org.dauch.lifecycle.Stop

final class Closer private() {

  private final var stops = List.empty[(AnyRef, Stop[AnyRef])]

  private def add[T <: AnyRef : Stop](s: T): Unit = {
    val ss = s.asInstanceOf[AnyRef]
    val st = summon[Stop[T]].asInstanceOf[Stop[AnyRef]]
    val h = ss -> st
    synchronized {
      stops = h :: stops
    }
  }

  def apply[T <: AnyRef : Stop](s: => T): T = {val ss = s; add(ss); ss }
  def use[T <: AnyRef : Stop](s: => T): Unit = add(s)
  
  def register(code: => Unit): Unit = {
    val ac: AutoCloseable = () => code
    use(ac)
  } 

  private def close(error: Throwable): Unit = synchronized {
    var e = error
    while (stops ne Nil) {
      val (v, s) :: t = stops
      try {
        s.stop(v)
      } catch {
        case x: Throwable => if (e == null) e = x else e.addSuppressed(x)
      }
      stops = t
    }
    if (e ne null) {
      throw e
    }
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
