package org.dauch.ide.main

import javafx.scene.Scene
import org.dauch.ide.Ide.PrimaryStage
import org.dauch.lifecycle.HasInit

final class IdeScene(using
  primaryStage: PrimaryStage,
  idePane: IdePane
) extends Scene(idePane, 800, 600) with HasInit {
  override def init(): Unit = {
    primaryStage.setScene(this)
    primaryStage.show()
  }
}
