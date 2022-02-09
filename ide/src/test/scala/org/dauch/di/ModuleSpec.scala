package org.dauch.di

import org.dauch.di.ModuleSpec.{CloseInReversedOrder, InitInDeclarationOrder, InitWithImplicits}
import org.dauch.lifecycle.HasInit
import org.scalatest.matchers.should.*
import org.scalatest.wordspec.*

import java.nio.file.Path
import scala.collection.immutable.Queue
import scala.util.Using
import scala.util.Using.resource

final class ModuleSpec extends AnyWordSpec with Matchers {
  "Module" should {

    "init in a declaration order" in {
      resource(InitInDeclarationOrder.TestModule1()) { m =>
        m.start()
        m.queue shouldBe Queue("i11", "i12", "i21", "i22")
      }
    }

    "close in a reversed order" in {
      val m = CloseInReversedOrder.TestModule1()
      resource(m) { m =>
        m.start()
      }
      m.queue shouldBe Queue("c22", "c21", "c12", "c11")
    }

    "initialize a module with implicits" in {
      val i = InitWithImplicits()
      resource(new i.TestModule())(_.start())
      i.inits shouldBe Queue("s1", "s2")
      i.closes shouldBe Queue("s2", "s1")
    }
  }
}

object ModuleSpec {

  object InitInDeclarationOrder {
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

  object CloseInReversedOrder {

    class Closeable(onClose: => Unit) extends AutoCloseable {
      override def close(): Unit = onClose
    }

    trait A
    trait B

    trait T1 { this: TestModule1 =>
      given t11: H[Closeable & A] = bind("T11")(new Closeable({ queue = queue.appended("c11") }) with A)
      given t12: H[Closeable & B] = bind("T12")(new Closeable({ queue = queue.appended("c12") }) with B)
      locally {
        init(t11)
        init(t12)
      }
    }

    trait T2 extends T1 { this: TestModule1 =>
      given t21: H[Closeable & A] = bind("T11")(new Closeable({ queue = queue.appended("c21") }) with A)
      given t22: H[Closeable & B] = bind("T12")(new Closeable({ queue = queue.appended("c22") }) with B)
      locally {
        init(t21)
        init(t22)
      }
    }

    final class TestModule1 extends Module("test") with T1 with T2 {
      private[di] var queue = Queue.empty[String]
    }
  }

  final class InitWithImplicits {

    private[di] var inits = Queue.empty[String]
    private[di] var closes = Queue.empty[String]

    class Service1 extends AutoCloseable with HasInit {
      override def init(): Unit = {
        inits = inits appended "s1"
      }

      override def close(): Unit = {
        closes = closes appended "s1"
      }
    }

    class Service2()(using s1: Service1) extends AutoCloseable with HasInit {
      override def init(): Unit = {
        inits = inits appended "s2"
      }

      override def close(): Unit = {
        closes = closes appended "s2"
      }
    }

    final class TestModule extends Module("test") {
      given s1: H[Service1] = bind("s1")(new Service1)
      given s2: H[Service2] = bind("s2")(new Service2())
      locally {
        init(s2)
      }
    }
  }
}
