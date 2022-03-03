package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import cats.kernel.Monoid

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.chaining._
import io.craigmiller160.akka.function.ListExt._

object BankAccountExercise extends App {
  val actorSystem = ActorSystem("bankAccountSystem")
  val account = actorSystem.actorOf(Props[BankAccount], "account")
  val owner = actorSystem.actorOf(AccountOwner.withAccount(account), "owner")

  owner ! BankAccountRequest(BankOperation.DEPOSIT, 100)
  owner ! BankAccountRequest(BankOperation.WITHDRAWAL, 50)
  owner ! BankAccountRequest(BankOperation.WITHDRAWAL, 80)
  owner ! BankAccountStatementRequest()

  actorSystem.terminate()
    .map(_ => println("ActorSystem terminated"))
}

object BankOperation extends Enumeration {
  type BankOperation = Value
  val DEPOSIT: BankOperation = Value
  val WITHDRAWAL: BankOperation = Value
}

case class BankAccountStatementRequest()
case class BankAccountRequest(operation: BankOperation.Value, amount: Double)
case class BankAccountResponse(operation: BankOperation.Value, amount: Double, balance: Double)

abstract class BankAccountException(name: String, response: BankAccountResponse) extends RuntimeException(s"$name: Operation: ${response.operation} Amount: ${response.amount} Balance: ${response.balance}")
case class InsufficientFundsException(response: BankAccountResponse) extends BankAccountException("InsufficientFunds", response)

object Transaction {
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
  def format(timestamp: LocalDateTime): String =
    formatter.format(timestamp)
}
case class Transaction(timestamp: LocalDateTime, operation: BankOperation.Value, amount: Double, startBalance: Double, endBalance: Double)
case class Statement(timestamp: LocalDateTime, balance: Double, transactions: List[Transaction])

class BankAccount extends Actor {
  var balance: Double = 0
  var transactions: List[Transaction] = List()

  private def newTransaction(operation: BankOperation.Value, amount: Double, startBalance: Double, endBalance: Double): Unit = {
    transactions = Transaction(LocalDateTime.now(), operation, amount, startBalance, balance) :: transactions
  }

  override def receive: Receive = {
    case BankAccountRequest(BankOperation.DEPOSIT, amount) =>
      val startBalance = balance
      balance += amount
      newTransaction(BankOperation.DEPOSIT, amount, startBalance, balance)
      sender() ! Success(BankAccountResponse(BankOperation.DEPOSIT, amount, balance))
    case BankAccountRequest(BankOperation.WITHDRAWAL, amount)
      if (amount > balance) =>
      sender() ! Failure(InsufficientFundsException(BankAccountResponse(BankOperation.WITHDRAWAL, amount, balance)))
    case BankAccountRequest(BankOperation.WITHDRAWAL, amount) =>
      val startBalance = balance
      balance -= amount
      newTransaction(BankOperation.WITHDRAWAL, amount, startBalance, balance)
      sender() ! Success(BankAccountResponse(BankOperation.WITHDRAWAL, amount, balance))
    case BankAccountStatementRequest() =>
      println("Received Statement")
      sender() ! Success(Statement(LocalDateTime.now(), balance, transactions.reverse))
  }
}
object Monoids {
  val stringMonoid = new Monoid[String] {
    override def empty: String = ""

    override def combine(x: String, y: String): String = x match {
      case "" => y
      case _ => s"$x\n$y"
    }
  }
}

object AccountOwner {
  def withAccount(account: ActorRef): Props = Props(new AccountOwner(account))
}
class AccountOwner(account: ActorRef) extends Actor {
  override def receive: Receive = {
    case BankAccountRequest(operation, amount) =>
      println(s"Sending Request: Operation: $operation Amount: $amount")
      account ! BankAccountRequest(operation, amount)
    case r: BankAccountStatementRequest =>
      println(s"Sending Request for statement")
      account ! r
    case Success(BankAccountResponse(operation, amount, balance)) =>
      println(s"Request successful: Operation: $operation Amount: $amount Balance: $balance")
    case Success(Statement(timestamp, balance, transactions)) =>
      val txns = transactions.map(txn => s"Timestamp: ${Transaction.format(txn.timestamp)} Operation: ${txn.operation} Amount: ${txn.amount} Start Balance: ${txn.startBalance} End Balance: ${txn.endBalance}")
        .foldM(Monoids.stringMonoid)
      println(s"Statement Request successful. Timestamp: $timestamp Balance: $balance Transactions:\n$txns")
    case Failure(ex: BankAccountException) =>
      println(s"Request failed: ${ex.getMessage}")
  }
}
