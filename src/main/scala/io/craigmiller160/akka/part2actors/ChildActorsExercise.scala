package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, Props}
import io.craigmiller160.akka.utils.ActorSystemHandler

object ChildActorsExercise extends App {
  ActorSystemHandler.useSimpleSystem("childActorExercise", system => {
    val text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    val master = system.actorOf(Props[WordCounterMaster])

    master ! text
  })
}

object WordCountMessage {
  case class Initialize(nChildren: Int)
  case class Task(text: String)
  case class Reply(count: Int)
}
class WordCounterMaster extends Actor {
  override def receive: Receive = ???
}
class WordCounterWorker extends Actor {
  override def receive: Receive = ???
}