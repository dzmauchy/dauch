package org.dauch.ide

import javafx.application.Application

import java.lang.module.{Configuration, ModuleFinder}
import scala.annotation.varargs

object IdeLauncher {
  @varargs def main(args: String*): Unit = {
    Application.launch(classOf[Ide], args *)
  }
}
