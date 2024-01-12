package org.dauch.di

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ApplicationSpec extends AnyWordSpec with Matchers {
  
  "An application with 1 module" should {
    "close the module on close" in {
      given app: Application = Application("app1")
      given mod: Context = Context("mod1")
      object Conf extends Configuration {
        var closed = false
        val service: AutoCloseable = bean("bean1")(() => closed = true)
      }

      Conf.closed should equal (false)
      app.modules shouldBe List(mod)
      mod.disposables should have size 1
      app.close()
      Conf.closed should equal (true)
    }
  }
}
