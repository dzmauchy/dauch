package org.dauch.di.exception

final class BeanInitializationException(cause: Throwable) 
  extends RuntimeException("Bean initialization error", cause, true, false)
