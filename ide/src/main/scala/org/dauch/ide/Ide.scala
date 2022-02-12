package org.dauch.ide

import com.sun.javafx.css.StyleManager
import javafx.application.{Application, Platform}
import javafx.stage.Stage
import org.dauch.di.HModule
import org.dauch.fx.FxImplicits.runLater
import org.dauch.ide.Ide.{IdeMainModule, PrimaryStage}
import org.dauch.ide.modules.IdeModule

import scala.annotation.varargs

final class Ide extends Application {

  override def init(): Unit = {
    val classLoader = Thread.currentThread().getContextClassLoader
    val themeUrl = classLoader.getResource("ui/theme.css")
    runLater {
      Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN)
      val styleManager = StyleManager.getInstance()
      styleManager.addUserAgentStylesheet(themeUrl.toString)
    }
  }

  override def start(primaryStage: Stage): Unit = {
    // init
    primaryStage.setTitle("IDE")
    primaryStage.setMaximized(true)

    given PrimaryStage = PrimaryStage(primaryStage)
    val module = new IdeMainModule()
    module.start()
  }
}

object Ide {

  opaque type PrimaryStage = Stage
  object PrimaryStage {
    def apply(s: Stage): PrimaryStage = s
    given Conversion[PrimaryStage, Stage] = identity
  }

  final class IdeMainModule(using ps: PrimaryStage) extends HModule("ide") with IdeModule
}
