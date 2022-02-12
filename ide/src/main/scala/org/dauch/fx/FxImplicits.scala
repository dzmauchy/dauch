package org.dauch.fx

import javafx.application.Platform
import org.dauch.concurrent.*

import java.util.concurrent.CompletableFuture
import scala.concurrent.{Future, Promise}
import scala.util.Try

object FxImplicits {

  def runLater(code: => Unit): Unit = Platform.runLater(() => code)

  def runLaterF[R](code: => R): CFuture[R] = {
    val cf = CFuture[R]()
    Platform.runLater(() =>
      try {
        cf.complete(code)
      } catch {
        case e: Throwable => cf.fail(e)
      }
    )
    cf
  }
}
