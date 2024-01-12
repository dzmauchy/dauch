package org.dauch.di

import org.dauch.di.exception.BeanConstructionException

trait Configuration(using val mod: Context) {

  final def bean[T](id: String)(f: => T): T = {
    try {
      val bean = f
      bean match {
        case c: AutoCloseable => mod.add(id, c)
      }
      bean match {
        case i: Initializable => i.init()
      }
      bean match {
        case c: EventConsumer => mod.add(id, c)
      }
      bean
    } catch {
      case e: Throwable =>
        val ex = BeanConstructionException(mod.app.id, mod.id, id, e)
        try {
          mod.close()
        } catch {
          case x: Throwable => ex.addSuppressed(x)
        }
        throw ex
    }
  }
  
  def register(c: => AnyRef): Unit = mod.addEager(() => c)
  
  protected def eagerBeans(): Unit = {
  }
}
