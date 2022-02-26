package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorSystem}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import BankOperation._

object BankAccountExercise extends App {
  val actorSystem = ActorSystem("bankAccountSystem")

  actorSystem.terminate()
    .map(_ => println("ActorSystem terminated"))
}

object BankOperation extends Enumeration {
  type BankOperation = Value
  val DEPOSIT: BankOperation = Value
  val WITHDRAWAL: BankOperation = Value
  val STATEMENT: BankOperation = Value
}

case class BankAccountRequest(operation: BankOperation, amount: Double)
case class BankAccountResponse(operation: BankOperation, amount: Double, balance: Double)

abstract class BankAccountException(response: BankAccountResponse) extends RuntimeException(s"${getClass.getSimpleName}: Operation: ${response.operation} Amount: ${response.amount} Balance: ${response.balance}")
case class InsufficientFundsException(response: BankAccountResponse) extends BankAccountException(response)

class BankAccount extends Actor {
  var balance = 0

  override def receive: Receive = {
    case BankAccountRequest(operation, amount) if (operation == BankOperation.DEPOSIT) =>
      balance += amount
      sender() ! Success(BankAccountResponse(operation, amount, balance))
    case BankAccountRequest(operation, amount)
      if (operation == BankOperation.WITHDRAWAL && amount > balance) =>
      sender() ! Failure(InsufficientFundsException(BankAccountResponse(operation, amount, balance)))
    case BankAccountRequest(operation, amount) if (operation == BankOperation.WITHDRAWAL) =>
      balance -= amount
      sender() ! Success(BankAccountResponse(operation, amount, balance))
  }
}
