package org.dauch.di

import org.dauch.di.exception.{BeanCloseException, BeanEventPublishingException, EventPublishingException, ModuleCloseException}

final class Module(val id: String)(using val app: Application) extends AutoCloseable {

  private var disposables = List.empty[(String, String, AutoCloseable)]
  private var eventConsumers = List.empty[(String, String, EventConsumer)]

  app.add(this)

  def publish(ev: AnyRef): Unit = {
    val ex = EventPublishingException()
    for ((ctx, id, c) <- eventConsumers) {
      try {
        c.consume(ev)
      } catch {
        case e: Throwable => ex.addSuppressed(BeanEventPublishingException(app.id, this.id, ctx, id, e))
      }
    }
    if (ex.getSuppressed.nonEmpty) throw ex
  }

  private[di] def add(ctx: Configuration, id: String, c: AutoCloseable): Unit = synchronized {
    disposables = (ctx.id, id, c) :: disposables
  }

  private[di] def add(ctx: Configuration, id: String, c: EventConsumer): Unit = {
    app.add(id, ctx.id, id, c)
    synchronized {
      eventConsumers = (ctx.id, id, c) :: eventConsumers
    }
  }

  override def close(): Unit = {
    app.remove(this)
    val ex = ModuleCloseException(app.id, id)
    synchronized {
      while (disposables.nonEmpty) {
        disposables = disposables match {
          case (ctx, id, c) :: t =>
            try {
              c.close()
            } catch {
              case e: Throwable => ex.addSuppressed(BeanCloseException(ctx, id, e))
            }
            t
          case Nil => Nil
        }
      }
    }
    if (ex.getSuppressed.nonEmpty) throw ex
  }
}
