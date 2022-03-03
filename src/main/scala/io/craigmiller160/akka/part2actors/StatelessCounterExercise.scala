package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import io.craigmiller160.akka.utils.ActorSystemHandler

import akka.actor.Actor

object StatelessCounterExercise extends App {
  ActorSystemHandler.useSimpleSystem("statelessCounter", system => {

  })
}

object CounterAction extends Enumeration {
  type CounterAction = Value

  val INCREMENT, DECREMENT, PRINT, RESET = Value
}

class StatelessCounter extends Actor {
  override def receive: Receive = count(0)
  private def count(theCount: Int): Receive = {
    case CounterAction.INCREMENT => context.become(count(theCount + 1))
    case CounterAction.DECREMENT => context.become(count(theCount - 1))
    case CounterAction.RESET => context.become(count(0))
    case CounterAction.PRINT =>
      println(s"The Count: $theCount")
  }
}
