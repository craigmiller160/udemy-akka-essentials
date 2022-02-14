package io.craigmiller160.akka
package io.craigmiller160.akka.part1recap

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object AdvancedRecap extends App {
  val partialFunction: PartialFunction[Int,Int] = {
    case 1 => 42
    case 2 => 65
    case 5 => 999
  }

  println(partialFunction(1))
  println(partialFunction(4))


  val future = Future {
    println("Hello Future")
  }
}
