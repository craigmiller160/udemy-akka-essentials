package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorSystem, Props}
import scala.concurrent.ExecutionContext.Implicits.global

object ActorCapabilities extends App {
  val actorSystem = ActorSystem("actorCapabilitiesDemo")
  val simpleActor = actorSystem.actorOf(Props[SimpleActor], "simpleActor")

  simpleActor ! "Hello, actor"

  actorSystem.terminate()
    .map(_ => println("ActorSystem terminated"))
}

class SimpleActor extends Actor {
  override def receive: Receive = {
    case message: String => println(s"[simple actor] I have received $message")
  }
}