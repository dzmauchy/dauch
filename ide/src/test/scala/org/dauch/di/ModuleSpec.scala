package org.dauch.di

import org.dauch.di.ModuleSpec.BindSimpleObject
import org.scalatest.matchers.should.*
import org.scalatest.wordspec.*

import java.nio.file.Path
import scala.collection.immutable.Queue
import scala.util.Using
import scala.util.Using.resource

final class ModuleSpec extends AnyWordSpec with Matchers {
  "Module" should {
    "bind simple object" in {
      resource(BindSimpleObject.TestModule1()) { m =>
        m.start()
        m.queue shouldBe Queue("i11", "i12", "i21", "i22")
      }
    }
  }
}

object ModuleSpec {

  object BindSimpleObject {
    trait T1 { this: TestModule1 =>
      locally {
        init("i11") { queue = queue.appended("i11") }
        init("i12") { queue = queue.appended("i12") }
      }
    }

    trait T2 extends T1 { this: TestModule1 =>
      locally {
        init("i21") { queue = queue.appended("i21") }
        init("i22") { queue = queue.appended("i22") }
      }
    }

    final class TestModule1 extends Module("test") with T1 with T2 {
      private[di] var queue = Queue.empty[String]
    }
  }
}
