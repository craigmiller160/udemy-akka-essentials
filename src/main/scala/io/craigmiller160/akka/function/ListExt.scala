package io.craigmiller160.akka
package io.craigmiller160.akka.function

import cats.kernel.Monoid

object ListExt {
  implicit class ListFnOps[A](list: List[A]) {
    def foldM(implicit monoid: Monoid[A]): A = monoid.combineAll(list)
  }
}
