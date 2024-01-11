package org.dauch.di

import org.dauch.di.exception.{BeanEventPublishingException, EventPublishingException}

final class Application(val id: String) {
  
  private var eventConsumers = List.empty[(String, String, String, EventConsumer)]
  
  private[di] def add(mod: String, ctx: String, id: String, c: EventConsumer): Unit = synchronized {
    eventConsumers = (mod, ctx, id, c) :: eventConsumers
  }
  
  def publish(ev: AnyRef): Unit = {
    var errors = Vector.empty[Throwable]
    for ((mod, ctx, id, c) <- eventConsumers) {
      try {
        c.consume(ev)
      } catch {
        case e: Throwable => errors = errors.appended(BeanEventPublishingException(this.id, mod, ctx, id, e))
      }
    }
    if (errors.nonEmpty) {
      val ex = EventPublishingException()
      errors.foreach(ex.addSuppressed)
      throw ex
    }
  }
}
