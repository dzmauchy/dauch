package org.dauch.lifecycle

trait Init[-T <: AnyRef] {
  def initialize(o: T): Unit
}

object Init {

  final val Empty: Init[AnyRef] = _ => ()

  inline given Init[Thread] = _.start()
  inline given Init[AnyRef] = Empty
}
