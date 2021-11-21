package org.dauch.marid.types

import org.dauch.marid.types.MType.ResolveResult
import org.dauch.marid.types.MType.ResolveResult.Error

import scala.collection.immutable.TreeMap

final class MTypeResolver {

  private var classes0 = TreeMap.empty[String, MClass.Desc]

  def register(c: MClass.Desc): Unit = classes0 = classes0.updated(c.c.name, c)

  def resolve(className: String, func: String, args: (String, MType)*): ResolveResult = {
    classes0.get(className) match {
      case Some(desc) => desc.func(func) match {
        case Some(f) => null
        case None => Error(desc.c.name, func, "No such func")
      }
      case None => Error(className, func, "No such class")
    }
  }

  def resolve(target: MType, func: String, args: (String, MType)*): ResolveResult = {
    null
  }
}
