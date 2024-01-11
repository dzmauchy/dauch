package org.dauch.di.exception

final class BeanConstructionException(app: String, mod: String, ctx: String, id: String, cause: Throwable) 
  extends RuntimeException(s"$app/$mod/$ctx/$id", cause, true, false) 
