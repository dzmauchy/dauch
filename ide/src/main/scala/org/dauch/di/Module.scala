package org.dauch.di

import org.dauch.lifecycle.{Init, Stop}

import scala.{Function, PartialFunction}

class Module(final val name: String) extends AutoCloseable {

  private final var hs = List.empty[H[?]]

  def bind[T <: AnyRef : Init : Stop](name: String)(s: => T): H[T] = {
    try {
      val h = new H(this, name, s, summon[Init[T]], summon[Stop[T]])
      synchronized(hs ::= h)
      h
    } catch {
      case e: Throwable =>
        close0(throw new IllegalStateException(s"Cannot create $name in $this", e))
        throw e
    }
  }

  override def close(): Unit = close0()

  private[di] def close0(ex: Throwable = null): Unit = synchronized {
    var e = ex
    while (hs ne Nil) {
      val h :: t = hs
      try {
        h.close()
      } catch {
        case x: Throwable => if (e == null) e = x else e.addSuppressed(x)
      }
      hs = t
    }
    if (e != null) {
      throw e
    }
  }

  override def toString: String = name

  inline given [T <: AnyRef]: Conversion[H[T], T] = h => h.get()
}
