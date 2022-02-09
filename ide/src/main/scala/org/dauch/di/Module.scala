package org.dauch.di

import org.dauch.lifecycle.{Init, Stop}

import scala.collection.immutable.Queue

class Module(final val name: String) extends AutoCloseable {

  private final var hs = List.empty[H[?]]
  private final var inits = Queue.empty[Runnable]

  def bind[T <: AnyRef : Init : Stop](name: String)(s: => T): H[T] = {
    var ref: H[T] = null
    val init: Init[T] = o => {
      synchronized(hs ::= ref)
      summon[Init[T]].initialize(o)
    }
    ref = new H(name, s, init, summon[Stop[T]])
    ref
  }

  def init(h: H[?]): Unit = synchronized {
    inits = inits.appended(() => try h.get() catch {
      case e: Throwable => close0(new IllegalStateException(s"Unable to init $name", e))
    })
  }

  def init(name: String)(code: => Unit): Unit = synchronized {
    inits = inits.appended(() => try code catch {
      case e: Throwable => close0(new IllegalStateException(s"Unable to init ${this.name}.$name", e))
    })
  }

  final def start(): Unit = synchronized {
    while (inits.nonEmpty) {
      val (h, t) = inits.dequeue
      try {
        h.run()
      } catch {
        case e: Throwable => close0(e)
      }
      inits = t
    }
  }

  override def close(): Unit = close0()

  private def close0(ex: Throwable = null): Unit = synchronized {
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

  inline given[T <: AnyRef]: Conversion[H[T], T] = h => h2t(using h)
  given h2t[T <: AnyRef](using h: H[T]): T = try h.get() catch {
    case e: Throwable => throw new IllegalStateException(s"Unable to get $name.${h.name}", e)
  }
}
