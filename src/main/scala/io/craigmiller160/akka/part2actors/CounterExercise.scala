package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorSystem, Props}

import scala.concurrent.ExecutionContext.Implicits.global
import CounterAction._

object CounterExercise extends App {
  val actorSystem = ActorSystem("counterSystem")
  val counter = actorSystem.actorOf(Props[Counter], "counter")

  counter ! CounterAction.INCREMENT
  counter ! CounterAction.INCREMENT
  counter ! CounterAction.INCREMENT
  counter ! CounterAction.DECREMENT
  counter ! CounterAction.PRINT

  val foo: CounterAction = CounterAction.DECREMENT

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
    case CounterAction.INCREMENT => count += 1
    case CounterAction.DECREMENT => count -= 1
    case CounterAction.PRINT => println(s"Count is $count")
  }
}
