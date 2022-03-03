package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorSystem, Props}

import scala.concurrent.ExecutionContext.Implicits.global

object CounterExercise extends App {
  val actorSystem = ActorSystem("counterSystem")
  val counter = actorSystem.actorOf(Props[Counter], "counter")

  counter ! StatelessCounterAction.INCREMENT
  counter ! StatelessCounterAction.INCREMENT
  counter ! StatelessCounterAction.INCREMENT
  counter ! StatelessCounterAction.DECREMENT
  counter ! StatelessCounterAction.PRINT

  val foo: StatelessCounterAction.Value = StatelessCounterAction.DECREMENT

  actorSystem.terminate()
    .map(_ => println("ActorSystem terminated"))
}

object CounterAction extends Enumeration {
  type CounterAction = Value

  val INCREMENT, DECREMENT, PRINT = Value
}

class Counter extends Actor {
  var count = 0

  override def receive: Receive = {
    case StatelessCounterAction.INCREMENT => count += 1
    case StatelessCounterAction.DECREMENT => count -= 1
    case StatelessCounterAction.PRINT => println(s"Count is $count")
  }
}
