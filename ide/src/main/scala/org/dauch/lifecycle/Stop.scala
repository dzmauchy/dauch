package org.dauch.lifecycle

import javafx.application.Platform
import javafx.application.Platform.{isFxApplicationThread, runLater}
import javafx.concurrent.Task
import javafx.scene.control.Dialog
import javafx.stage.{Stage, Window}
import org.dauch.lifecycle.Stop.*
import org.dauch.lifecycle.Stops

import java.util.TimerTask
import java.util.concurrent.{ExecutorService, ScheduledFuture, TimeUnit}

trait Stop[-T <: AnyRef] {
  def stop(o: T): Unit
}

object Stop {

  final val Empty: Stop[AnyRef] = _ => ()

  inline given Stop[Thread] = Stops.stop(_)
  inline given Stop[ExecutorService] = Stops.stop(_)
  inline given Stop[Dialog[?]] = d => if (isFxApplicationThread) d.close() else runLater(() => d.close())
  inline given Stop[Window] = w => if (isFxApplicationThread) w.hide() else runLater(() => w.hide())
  inline given Stop[Stage] = s => if (isFxApplicationThread) s.close() else runLater(() => s.close())
  inline given Stop[Task[?]] = Stops.stop(_)
  inline given Stop[TimerTask] = _.cancel()
  inline given Stop[ScheduledFuture[?]] = Stops.stop(_)
  inline given Stop[AutoCloseable] = _.close()
  inline given Stop[AnyRef] = Empty
}
