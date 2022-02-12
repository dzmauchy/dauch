package org.dauch.concurrent

import java.util.concurrent.{CompletableFuture, CompletionStage, Executor, ExecutorService}

opaque type AsyncExecutor = Executor
object AsyncExecutor {
  inline def apply(s: Executor): AsyncExecutor = s
  inline def apply(s: CompletableFuture[?]): AsyncExecutor = apply(s.defaultExecutor())
}

opaque type CStage[+T] = CompletionStage[T]
object CStage {
  inline def apply[T](s: CompletionStage[T]): CStage[T] = s
}

opaque type CFuture[+T] = CompletableFuture[T]
object CFuture {
  inline def apply[T](f: CompletableFuture[T]): CFuture[T] = f
  inline def apply[T](): CFuture[T] = new CompletableFuture[T]()
}
extension [T](f: CFuture[T]) {
  inline def executor: AsyncExecutor = f.defaultExecutor()
  inline def toFuture: CompletableFuture[T] = f
  inline def complete(v: T): Boolean = f.complete(v)
  inline def fail(e: Throwable): Boolean = f.completeExceptionally(e)
  inline def map[R](c: T => R): CFuture[R] = f.thenApply(c(_))
  inline def mapAsync[R](c: T => R)(using ae: AsyncExecutor): CFuture[R] = f.thenApplyAsync(c(_), ae)
  inline def mapA[R](c: T => R): CFuture[R] = f.thenApplyAsync(c(_))
  inline def flatMap[R](c: T => CStage[R]): CFuture[R] = f.thenCompose(c(_))
  inline def flatMapAsync[R](c: T => CStage[R])(using ae: AsyncExecutor): CFuture[R] = f.thenComposeAsync(c(_), ae)
  inline def flatMapA[R](c: T => CStage[R]): CFuture[R] = f.thenComposeAsync(c(_))
}

object ConcurrentImplicits {

}
