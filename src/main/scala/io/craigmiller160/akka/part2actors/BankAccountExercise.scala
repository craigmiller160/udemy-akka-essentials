package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorSystem}

import scala.concurrent.ExecutionContext.Implicits.global

object BankAccountExercise extends App {
  val actorSystem = ActorSystem("bankAccountSystem")

  actorSystem.terminate()
    .map(_ => println("ActorSystem terminated"))
}

case class Deposit(amount: Double)
case class Withdraw(amount: Double)
case class Statement()

class BankAccount extends Actor {
  var balance = 0

  override def receive: Receive = {
    case Deposit(amount) => ???
    case Withdraw(amount) => ???
    case Statement => ???
  }
}
