package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import io.craigmiller160.akka.utils.ActorSystemHandler

import akka.actor.{Actor, ActorRef, Props}

object ChangingActorBehavior extends App {
  ActorSystemHandler.useSimpleSystem("changeActorBehavior", system => {
    val fussyKid = system.actorOf(Props[FussyKid])
    val mom = system.actorOf(Props[Mom])

    mom ! Mom.MomStart(fussyKid)
  })
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
    case Mom.Ask(_) => state match {
      case HAPPY => sender() ! KidAccept
      case SAD => sender() ! KidReject
    }
  }
}

class StatelessFussyKid extends Actor {
  import FussyKid._
  override def receive: Receive = happyReceive

  private def happyReceive: Receive = {
    case Mom.Food(Mom.VEGETABLE) => context.become(sadReceive)
    case Mom.Food(Mom.CHOCOLATE) => // happy to stay happy
    case Mom.Ask(_) => sender() ! KidAccept
  }
  private def sadReceive: Receive = {
    case Mom.Food(Mom.VEGETABLE) => // sad so stay sad
    case Mom.Food(Mom.CHOCOLATE) => context.become(happyReceive)
    case Mom.Ask(_) => sender() ! KidReject
  }
}

object Mom {
  case class MomStart(kid: ActorRef)
  case class Food(food: String)
  case class Ask(message: String)
  val VEGETABLE = "veggies"
  val CHOCOLATE = "chocolate"
}
class Mom extends Actor {
  import Mom._
  override def receive: Receive = {
    case MomStart(kid) =>
      kid ! Food(VEGETABLE)
      kid ! Ask("do you want to play?")
    case FussyKid.KidAccept => println("Yay, my kid is happy!")
    case FussyKid.KidReject => println("My kid is sad, but at least he's healthy!")
  }
}


