package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.Actor
import io.craigmiller160.akka.utils.ActorSystemHandler

object ChildActorsExercise extends App {
  ActorSystemHandler.useSimpleSystem("childActorExercise", system => {

  })
}

object WordCounterMaster {
  case class Initialize(nChildren: Int)
  case class WordCountTask(text: String)
  case class WordCountReply(count: Int)
}
class WordCounterMaster extends Actor {
  override def receive: Receive = ???
}
class WordCounterWorker extends Actor {
  override def receive: Receive = ???
}