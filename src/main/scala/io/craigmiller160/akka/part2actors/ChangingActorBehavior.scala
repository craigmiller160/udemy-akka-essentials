package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.Actor

object ChangingActorBehavior extends App {

}

object FussyKid {
  case object KidAccept
  case object KidReject
  val HAPPY = "happy"
  val SAD = "sad"
}
class FussyKid extends Actor {
  import FussyKid._
  var state: String = HAPPY
  override def receive: Receive = {
    case Mom.Food(food) => food match {
      case Mom.VEGETABLE => state = SAD
      case Mom.CHOCOLATE => state = HAPPY
    }
    case Mom.Ask => state match {
      case HAPPY => sender() ! KidAccept
      case SAD => sender() ! KidReject
    }
  }
}

object Mom {
  case class Food(food: String)
  case class Ask(message: String)
  val VEGETABLE = "veggies"
  val CHOCOLATE = "chocolate"
}
class Mom extends Actor {
  override def receive: Receive = ???
}


