package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorSystem, Props}

import scala.concurrent.ExecutionContext.Implicits.global

object CounterExercise extends App {
  val actorSystem = ActorSystem("counterSystem")
  val counter = actorSystem.actorOf(Props[Counter], "counter")

  counter ! CounterAction.INCREMENT
  counter ! CounterAction.INCREMENT
  counter ! CounterAction.INCREMENT
  counter ! CounterAction.DECREMENT
  counter ! CounterAction.PRINT

  actorSystem.terminate()
    .map(_ => println("ActorSystem terminated"))
}

object CounterAction extends Enumeration {
  type CounterAction = Value

  val INCREMENT: CounterAction = Value("INCREMENT")
  val DECREMENT: CounterAction = Value("DECREMENT")
  val PRINT: CounterAction = Value("PRINT")
}

class Counter extends Actor {
  var count = 0

  override def receive: Receive = {
    case CounterAction.INCREMENT => count += 1
    case CounterAction.DECREMENT => count -= 1
    case CounterAction.PRINT => println(s"Count is $count")
  }
}
