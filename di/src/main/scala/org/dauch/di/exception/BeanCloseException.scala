package org.dauch.di.exception

final class BeanCloseException(id: String, cause: Throwable) 
  extends RuntimeException(id, cause, true, false)
