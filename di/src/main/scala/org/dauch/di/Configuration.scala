package org.dauch.di

trait Configuration(id: String)(using val mod: Module) {

  val context: Context = Context(id)
}
