package org.dauch.di

import org.dauch.di.exception.{BeanCloseException, BeanEventPublishingException, EventPublishingException, ModuleCloseException}

import java.util.concurrent.Callable

final class Context(val id: String)(using val app: Application) extends AutoCloseable {

  private[di] var disposables = List.empty[(String, AutoCloseable)]
  private[di] var eventConsumers = List.empty[(String, EventConsumer)]
  private[di] var eagerBeans = List.empty[Callable[AnyRef]]

  app.add(this)

  def publish(ev: AnyRef): Unit = {
    val ex = EventPublishingException()
    for ((id, c) <- eventConsumers) {
      try {
        c.consume(ev)
      } catch {
        case e: Throwable => ex.addSuppressed(BeanEventPublishingException(app.id, this.id, id, e))
      }
    }
    if (ex.getSuppressed.nonEmpty) throw ex
  }

  private[di] def add(id: String, c: AutoCloseable): Unit = synchronized {
    disposables = (id, c) :: disposables
  }

  private[di] def add(id: String, c: EventConsumer): Unit = {
    app.add(this.id, id, c)
    synchronized {
      eventConsumers = (id, c) :: eventConsumers
    }
  }

  private[di] def addEager(b: Callable[AnyRef]): Unit = synchronized {
    eagerBeans = b :: eagerBeans
  }

  def start(): Unit = {
    var started = false
    while (!started) {
      val b = synchronized {
        eagerBeans match {
          case h :: t =>
            eagerBeans = t
            h
          case Nil => null  
        }
      }
      if (b ne null) {
        b.call()
      } else {
        started = true
      }
    }
  }

  override def close(): Unit = {
    app.remove(this)
    val ex = ModuleCloseException(app.id, id)
    synchronized {
      while (disposables.nonEmpty) {
        disposables = disposables match {
          case (id, c) :: t =>
            try {
              c.close()
            } catch {
              case e: Throwable => ex.addSuppressed(BeanCloseException(id, e))
            }
            t
          case Nil => Nil
        }
      }
    }
    if (ex.getSuppressed.nonEmpty) throw ex
  }
}
