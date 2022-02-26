package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorSystem}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object BankAccountExercise extends App {
  val actorSystem = ActorSystem("bankAccountSystem")

  actorSystem.terminate()
    .map(_ => println("ActorSystem terminated"))
}

case class Deposit(amount: Double)
case class Withdraw(amount: Double)
case class Statement()
case class InsufficientFundsException(balance: Double) extends RuntimeException(s"Insufficient funds. Balance: $balance")

class BankAccount extends Actor {
  var balance = 0

  override def receive: Receive = {
    case Deposit(amount) =>
      balance += amount
      Success(balance)
    case Withdraw(amount) if (amount > balance) =>
      Failure(new InsufficientFundsException(balance))
    case Withdraw(amount) =>
      balance -= amount
      Success(balance)
    case Statement => ???
  }
}
