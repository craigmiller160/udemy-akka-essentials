package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorRef, Props}
import io.craigmiller160.akka.utils.ActorSystemHandler

// TODO how to know if actor failed to process?
object ChildActorsExercise extends App {
  ActorSystemHandler.useSimpleSystem("childActorExercise", system => {
    val text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    val master = system.actorOf(Props[WordCounterMaster], "master")

    master ! WordCountMessage.Initialize(10)
    master ! text
  })
}

case class WordCounterState(workers: Set[ActorRef], pendingTasks: Int)

object WordCountMessage {
  case class Initialize(nChildren: Int)
  case class Task(text: String)
  case class Reply(count: Int)
}
class WordCounterMaster extends Actor {
  override def receive: Receive = ???

  private def init: Receive = {
    case WordCountMessage.Initialize(nChildren) =>
      val workers = (0 until nChildren)
        .map(index =>
          context.actorOf(Props[WordCounterWorker], s"worker-$index")
        )
        .toSet
      context.become(count(workers))
  }

  private def count(workers: Set[ActorRef]): Receive = {
    case text: String =>
      val parts = text.split("[,.;:?!]")
  }

  private def waitingForCount: Receive = {
    case WordCountMessage.Reply(count) =>
  }
}
class WordCounterWorker extends Actor {
  override def receive: Receive = {
    case WordCountMessage.Task(text) =>
      val wordCount = text.split(" ").size
      sender() ! WordCountMessage.Reply(wordCount)
  }
}