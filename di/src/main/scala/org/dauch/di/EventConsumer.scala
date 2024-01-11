package org.dauch.di

import java.util.EventListener

trait EventConsumer extends EventListener {
  def consume(ev: AnyRef): Unit
}
