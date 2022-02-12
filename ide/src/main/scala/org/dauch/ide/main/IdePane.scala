package org.dauch.ide.main

import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import org.dauch.ide.Ide.PrimaryStage
import org.dauch.lifecycle.HasInit

final class IdePane(using 
  tabPane: IdeTabPane
) extends BorderPane(tabPane) {
}
