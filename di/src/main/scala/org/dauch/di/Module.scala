package org.dauch.di

import org.dauch.di.exception.{BeanCloseException, BeanEventPublishingException, EventPublishingException, ModuleCloseException}

final class Module(val id: String)(using val app: Application) {

  private var disposables = List.empty[(String, String, AutoCloseable)]
  private var eventConsumers = List.empty[(String, String, EventConsumer)]

  def publish(ev: AnyRef): Unit = {
    var errors = Vector.empty[Throwable]
    for ((ctx, id, c) <- eventConsumers) {
      try {
        c.consume(ev)
      } catch {
        case e: Throwable => errors = errors.appended(BeanEventPublishingException(app.id, this.id, ctx, id, e))
      }
    }
    if (errors.nonEmpty) {
      val ex = EventPublishingException()
      errors.foreach(ex.addSuppressed)
      throw ex
    }
  }

  private[di] def add(ctx: Context, id: String, c: AutoCloseable): Unit = synchronized {
    disposables = (ctx.id, id, c) :: disposables
  }

  private[di] def add(ctx: Context, id: String, c: EventConsumer): Unit = {
    app.add(id, ctx.id, id, c)
    synchronized {
      eventConsumers = (ctx.id, id, c) :: eventConsumers
    }
  }

  private[di] def close(): Unit = {
    var errors = Vector.empty[Throwable]
    synchronized {
      while (disposables.nonEmpty) {
        disposables = disposables match {
          case (ctx, id, c) :: t =>
            try {
              c.close()
            } catch {
              case e: Throwable => errors = errors.appended(BeanCloseException(ctx, id, e))
            }
            t
          case Nil => Nil
        }
      }
    }
    if (errors.nonEmpty) {
      val ex = ModuleCloseException(app.id, id)
      errors.foreach(ex.addSuppressed)
      throw ex
    }
  }
}
