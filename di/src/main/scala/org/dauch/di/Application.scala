package org.dauch.di

import org.dauch.di.exception.{ApplicationCloseException, BeanEventPublishingException, EventPublishingException, ModuleCloseException}

final class Application(val id: String) extends AutoCloseable {

  private[di] var modules = List.empty[Context]
  private[di] var eventConsumers = List.empty[(String, String, EventConsumer)]

  private[di] def add(mod: String, id: String, c: EventConsumer): Unit = synchronized {
    eventConsumers = (mod, id, c) :: eventConsumers
  }

  private[di] def add(mod: Context): Unit = synchronized {
    modules = mod :: modules
  }

  private[di] def remove(mod: Context): Unit = synchronized {
    modules = modules.filterNot(_ eq mod)
  }
  
  def start(): Unit = {
    val ms = synchronized(modules.reverse)
    for (m <- ms) m.start()
  }

  def publish(ev: AnyRef): Unit = {
    val ex = EventPublishingException()
    for ((mod, id, c) <- eventConsumers) {
      try {
        c.consume(ev)
      } catch {
        case e: Throwable => ex.addSuppressed(BeanEventPublishingException(this.id, mod, id, e))
      }
    }
    if (ex.getSuppressed.nonEmpty) throw ex
  }

  override def close(): Unit = {
    val ex = ApplicationCloseException(id)
    var nonEmpty = true
    while (nonEmpty) {
      val m = synchronized {
        modules match {
          case h :: t => h
          case Nil => null
        }
      }
      if (m ne null) {
        try {
          m.close()
        } catch {
          case e : Throwable => ex.addSuppressed(ModuleCloseException(id, m.id))
        }
        nonEmpty = true
      } else {
        nonEmpty = false
      }
    }
    if (ex.getSuppressed.nonEmpty) throw ex
  }
}
