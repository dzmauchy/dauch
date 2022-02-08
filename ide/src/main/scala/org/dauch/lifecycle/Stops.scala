package org.dauch.lifecycle

import javafx.application.*
import javafx.concurrent.Task

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.LockSupport

object Stops {

  private val waitTime = Option(System.getProperty("dauch.stop.wait.time"))
    .flatMap(_.toLongOption)
    .getOrElse(60_000L)

  def stop(t: Thread): Unit = {
    try {
      t.join(waitTime)
    } catch {
      case e: InterruptedException =>
        t.interrupt()
        throw e
    } finally {
      if (t.isAlive) {
        t.interrupt()
      }
    }
  }

  def stop(s: ExecutorService): Unit = {
    s.shutdown()
    var r = false
    try {
      r = s.awaitTermination(waitTime, TimeUnit.MILLISECONDS)
    } catch {
      case e: InterruptedException =>
        s.shutdownNow()
        throw e
    } finally {
      if (!r) {
        s.shutdownNow()
      }
    }
  }

  def stop(t: Task[?]): Unit = {
    t.cancel()
    if (Platform.isFxApplicationThread) {
      val time = System.currentTimeMillis()
      while (System.currentTimeMillis() - time < waitTime) {
        if (!t.isRunning) {
          return
        }
        LockSupport.parkNanos(100_000L)
      }
      if (t.isRunning) {
        t.cancel(true)
      }
    } else {
      val time = System.currentTimeMillis()
      val terminated = new AtomicBoolean()
      while (System.currentTimeMillis() - time < waitTime) {
        if (terminated.get()) {
          return
        }
        Platform.runLater(() => terminated.set(!t.isRunning))
        LockSupport.parkNanos(100_000L)
      }
      if (!terminated.get()) {
        t.cancel(true)
      }
    }
  }

  def stop(f: ScheduledFuture[?]): Unit = {
    f.cancel(false)
    val time = System.currentTimeMillis()
    while (System.currentTimeMillis() - time < waitTime) {
      if (f.isDone) {
        return
      }
      LockSupport.parkNanos(100_000L)
    }
    if (!f.isDone) {
      f.cancel(true)
    }
  }
}
