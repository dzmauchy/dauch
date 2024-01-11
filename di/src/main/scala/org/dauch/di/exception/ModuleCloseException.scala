package org.dauch.di.exception

final class ModuleCloseException(app: String, mod: String) 
  extends RuntimeException(s"$app/$mod", null, true, false)
