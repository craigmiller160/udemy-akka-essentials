package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorSystem}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import BankOperation._

import java.time.LocalDateTime

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

case class Transaction(timestamp: LocalDateTime, operation: BankOperation, amount: Double, startBalance: Double, endBalance)

class BankAccount extends Actor {
  var balance = 0
  var transactions: List[Transaction] = List()

  private def newTransaction(operation: BankOperation, amount: Double, startBalance: Double, endBalance: Double): Unit = {
    transactions = Transaction(LocalDateTime.now(), operation, amount, startBalance, balance) :: transactions
  }

  override def receive: Receive = {
    case BankAccountRequest(operation, amount) if (operation == BankOperation.DEPOSIT) =>
      val startBalance = balance
      balance += amount
      newTransaction(operation, amount, startBalance, balance)
      sender() ! Success(BankAccountResponse(operation, amount, balance))
    case BankAccountRequest(operation, amount)
      if (operation == BankOperation.WITHDRAWAL && amount > balance) =>
      sender() ! Failure(InsufficientFundsException(BankAccountResponse(operation, amount, balance)))
    case BankAccountRequest(operation, amount) if (operation == BankOperation.WITHDRAWAL) =>
      val startBalance = balance
      balance -= amount
      newTransaction(operation, amount, startBalance, balance)
      sender() ! Success(BankAccountResponse(operation, amount, balance))
  }
}
