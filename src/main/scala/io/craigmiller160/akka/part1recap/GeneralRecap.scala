package io.craigmiller160.akka
package io.craigmiller160.akka.part1recap
import scalaz.Scalaz._

object GeneralRecap extends App {
  val aCondition = false

  val aConditionedVal = if (aCondition) 42 else 65

  val aCodeBlock = {

  }

  def aFunction(x: Int): Int = x + 1

  def twoArgs (x: Int)(y: Int): Int = x + y

  val result = aFunction(1) |> aFunction |> twoArgs(2)
  println(result)

  val forResult = for {
    num <- List(1,2,3)
    char <- List('a', 'b', 'c')
  } yield s"$num - $char"
  println(forResult)

  val forResult2 = for {
    value1 <- Some("Hello")
    value2 <- Some("World")
  } yield s"$value1 $value2"
  println(forResult2)
}
