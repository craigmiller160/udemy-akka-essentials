package io.craigmiller160.akka
package io.craigmiller160.akka.part1recap

object ThreadModelLimitations extends App {
  /*
  Daniel's Rants

  1) OOP encapsulation is only valid in the SINGLE THREADED MODEL
   */

  class BankAccount(private var amount: Int) {
    override def toString: String = s"$amount"
    def withdraw(money: Int): Unit = this.synchronized {
      this.amount -= money
    }
    def deposit(money: Int): Unit = this.synchronized {
      this.amount += money
    }
    def getAmount: Int = this.synchronized {
      this.amount
    }
  }

  val account = new BankAccount(2000)
//  (0 to 1000).foreach(_ => new Thread(() => account.withdraw(1)).start())
//  (0 to 1000).foreach(_ => new Thread(() => account.deposit(1)).start())
//  println(account.getAmount)

  // OOP encapsulation si broken in a multithreaded environment
  // synchronization/locks are required
  // New problems: deadlocks, livelocks, and performance problems

  /*
  2) Delegating something to a thread is a pain
   */

  // you have a running thread, and you want to pass a runnable to that thread, how?
  var task: Runnable = null
  val runningThread: Thread = new Thread(() => {
    while(true) {
      while(task == null) {
        runningThread.synchronized {
          println("[background] waiting for a task...")
          runningThread.wait()
        }
      }

      task.synchronized {
        println("[background] I have a task!")
        task.run()
        task = null
      }
    }
  })

  def delegateToBackgroundThread(r: Runnable): Unit = {
    if (task == null) {
      task = r
    }
    runningThread.synchronized {
      runningThread.notify()
    }
  }

  runningThread.start()
  Thread.sleep(500)
  delegateToBackgroundThread(() => println(42))
  Thread.sleep(1000)
  delegateToBackgroundThread(() => println("This should run in background"))
}
