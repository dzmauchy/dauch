package org.dauch.di

import org.dauch.lifecycle.{Init, Stop}

import scala.annotation.unchecked.uncheckedVariance

final class H[+T <: AnyRef](
  module: Module,
  name: String,
  v: => T,
  init: Init[T],
  stop: Stop[T]
) extends AutoCloseable {

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
                  module.close0(new IllegalStateException(s"Unable to start ${module.name}.$name", e))
                  throw e
              }
            case Some(v) => v
            case None => throw new IllegalStateException(s"Closed ${module.name}.$name")
          }
        }
      case Some(v) => v
      case None => throw new IllegalStateException(s"Closed ${module.name}.$name")
    }
  }

  def close(): Unit = value match {
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
