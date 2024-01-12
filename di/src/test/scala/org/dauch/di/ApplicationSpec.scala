package org.dauch.di

import org.scalatest.wordspec.AnyWordSpec

class ApplicationSpec extends AnyWordSpec {
  "An application with 1 module" should {
    "close the module on close" in {
      given app: Application = Application("app1")
      given mod: Context = Context("mod1")
      var closed = false
      object Conf extends Configuration {
        val service: AutoCloseable = bean("bean1")(() => closed = true)
      }
    }
  }
}
