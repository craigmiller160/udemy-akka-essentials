package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scalaz.{Foldable, Monoid}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    case BankAccountStatementRequest =>
      sender() ! Success(Statement(LocalDateTime.now(), balance, transactions.reverse))
  }
}

case class StringLineMonoid() extends Monoid[String] {
  override def zero: String = ""
  override def append(f1: String, f2: => String): String = f1 match {
    case "" => f2
    case _ => s"$f1\n$f2"
  }
}
object Monoids {
  val StringLine: Monoid[String] = StringLineMonoid()
}

object AccountOwner {
  def withAccount(account: ActorRef): Props = Props(new AccountOwner(account))
}
class AccountOwner(account: ActorRef) extends Actor {
  override def receive: Receive = {
    case BankAccountRequest(operation, amount) =>
      println(s"Sending Request: Operation: $operation Amount: $amount")
      account ! BankAccountRequest(operation, amount)
    case Success(BankAccountResponse(operation, amount, balance)) =>
      println(s"Request successful: Operation: $operation Amount: $amount Balance: $balance")
    case Success(Statement(timestamp, balance, transactions)) =>
      val txns = transactions.map(txn => s"Timestamp: ${Transaction.format(txn.timestamp)} Operation: ${txn.operation} Amount: ${txn.amount} Start Balance: ${txn.startBalance} End Balance: ${txn.endBalance}")
        .fold(Monoids.StringLine.zero)(Monoids.StringLine.append)
      println(s"Statement Request successful. Timestamp: $timestamp Balance: $balance Transactions:\n$txns")
    case Failure(ex: BankAccountException) =>
      println(s"Request failed: ${ex.getMessage}")
  }
}
