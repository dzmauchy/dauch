package org.dauch.di.exception

final class BeanCloseException(ctx: String, id: String, cause: Throwable) 
  extends RuntimeException(s"$ctx/$id", cause, true, false)
