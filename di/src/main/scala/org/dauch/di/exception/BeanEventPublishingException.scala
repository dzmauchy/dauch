package org.dauch.di.exception

final class BeanEventPublishingException(app: String, mod: String, id: String, cause: Throwable)
  extends RuntimeException(s"$app/$mod/$id", cause, true, false)
