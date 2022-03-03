package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import io.craigmiller160.akka.utils.ActorSystemHandler

import akka.actor.{Actor, Props}

object StatelessCounterExercise extends App {
  ActorSystemHandler.useSimpleSystem("statelessCounter", system => {
    val counter = system.actorOf(Props[StatelessCounter])

    counter ! StatelessCounterAction.INCREMENT
    counter ! StatelessCounterAction.DECREMENT
    counter ! StatelessCounterAction.INCREMENT
    counter ! StatelessCounterAction.INCREMENT
    counter ! StatelessCounterAction.PRINT
  })
}

object StatelessCounterAction extends Enumeration {
  type StatelessCounterAction = Value

  val INCREMENT, DECREMENT, PRINT, RESET = Value
}

class StatelessCounter extends Actor {
  override def receive: Receive = count(0)
  private def count(theCount: Int): Receive = {
    case StatelessCounterAction.INCREMENT => context.become(count(theCount + 1))
    case StatelessCounterAction.DECREMENT => context.become(count(theCount - 1))
    case StatelessCounterAction.RESET => context.become(count(0))
    case StatelessCounterAction.PRINT =>
      println(s"The Count: $theCount")
  }
}
