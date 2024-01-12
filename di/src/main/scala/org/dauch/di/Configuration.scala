package org.dauch.di

import org.dauch.di.exception.{BeanConstructionException, BeanInitializationException}

trait Configuration(val id: String)(using val mod: Module) {

  final def bean[T](id: String)(f: => T): T = {
    try {
      val bean = f
      bean match {
        case c: AutoCloseable => mod.add(this, id, c)
      }
      bean match {
        case i: Initializable =>
          try {
            i.init()
          } catch {
            case e: Throwable => throw BeanInitializationException(e)
          }
      }
      bean match {
        case c: EventConsumer => mod.add(this, id, c)
      }
      bean
    } catch {
      case e: Throwable =>
        val ex = BeanConstructionException(mod.app.id, mod.id, this.id, id, e)
        try {
          mod.close()
        } catch {
          case x: Throwable => ex.addSuppressed(x)
        }
        throw ex
    }
  }
}
