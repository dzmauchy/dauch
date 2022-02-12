package org.dauch.ide.modules

import org.dauch.di.{H, HModule}
import org.dauch.ide.Ide.PrimaryStage
import org.dauch.ide.main.{IdePane, IdeScene, IdeTabPane}

trait IdeModule(using s: PrimaryStage) { this: HModule =>
  
  given H[IdeTabPane] = bind("ideTabPane")(new IdeTabPane)
  given H[IdePane] = bind("idePane")(new IdePane)
  given H[IdeScene] = bind("ideScene")(new IdeScene)
  
  locally {
    init(given_H_IdeScene)
  }
}
