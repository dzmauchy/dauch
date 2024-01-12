package org.dauch.ide

import org.dauch.di.*

object Main {
  def main(args: Array[String]): Unit = {
    implicit val app: Application = Application("cv")
    println(app)
  }
}
