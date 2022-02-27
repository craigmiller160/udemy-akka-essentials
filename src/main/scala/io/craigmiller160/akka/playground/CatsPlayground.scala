package io.craigmiller160.akka
package io.craigmiller160.akka.playground

import cats.kernel.Monoid
//import cats.instances.list._
//import cats._
//import cats.implicits._
import io.craigmiller160.akka.function.ListExt._

object CatsPlayground extends App {
  val list: List[String] = List("Hello", "World", "Universe")

  val stringMonoid: Monoid[String] = new Monoid[String] {
    override def empty: String = ""

    override def combine(x: String, y: String): String = x match {
      case "" => y
      case _ => s"$x $y"
    }
  }

//  val result = list.fold(stringMonoid.empty)(stringMonoid.combine)
//  println(result)
//
//  val result2 = stringMonoid.combineAll(list)
//  println(result2)

  val result = list.foldM(stringMonoid)
  println(result)
}
