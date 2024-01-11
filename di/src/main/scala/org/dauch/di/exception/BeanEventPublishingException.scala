package org.dauch.di.exception

final class BeanEventPublishingException(app: String, mod: String, ctx: String, id: String, cause: Throwable)
  extends RuntimeException(s"$app/$mod/$ctx/$id", cause, true, false)
