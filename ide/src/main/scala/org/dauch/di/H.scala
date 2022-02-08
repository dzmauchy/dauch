package org.dauch.di

import org.dauch.lifecycle.{Init, Stop}

final class H[+T <: AnyRef](val name: String, v: => T, init: Init[T], stop: Stop[T]) {

  @volatile private var value: Option[T] = _

  def get(): T = {
    value match {
      case null =>
        synchronized {
          value match {
            case null =>
              val pv = v
              value = Some(pv)
              try {
                init.initialize(pv)
                pv
              } catch {
                case e: Throwable =>
                  throw new IllegalStateException(s"Unable to start $name", e)
              }
            case Some(v) => v
            case None => throw new IllegalStateException(s"Closed $name")
          }
        }
      case Some(v) => v
      case None => throw new IllegalStateException(s"Closed $name")
    }
  }

  private[di] def close(): Unit = value match {
    case null =>
    case Some(v) =>
      synchronized {
        value match {
          case null =>
          case Some(v) =>
            value = None
            stop.stop(v)
          case None =>
        }
      }
    case None =>
  }
}
