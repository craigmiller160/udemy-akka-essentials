package io.craigmiller160.akka
package io.craigmiller160.akka.playground

import cats.kernel.Monoid

object CatsPlayground extends App {
  val list: List[String] = List("Hello", "World", "Universe")

  val stringMonoid = new Monoid[String] {
    override def empty: String = ""

    override def combine(x: String, y: String): String = x match {
      case "" => y
      case _ => s"$x $y"
    }
  }

  val result = list.fold(stringMonoid.empty)(stringMonoid.combine)
  println(result)
}
