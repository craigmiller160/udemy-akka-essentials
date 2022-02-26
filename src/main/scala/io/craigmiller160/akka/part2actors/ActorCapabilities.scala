package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.concurrent.ExecutionContext.Implicits.global

object ActorCapabilities extends App {
  val actorSystem = ActorSystem("actorCapabilitiesDemo")
  val simpleActor = actorSystem.actorOf(Props[SimpleActor], "simpleActor")
  val alice = actorSystem.actorOf(Props[SimpleActor], "alice")
  val bob = actorSystem.actorOf(Props[SimpleActor], "bob")

  simpleActor ! "Hello, actor"
  simpleActor ! 42
  simpleActor ! SpecialMessage("some special content")
  simpleActor ! SendMessageToYourself("Send to yourself")

  alice ! SayHiTo(bob)

  actorSystem.terminate()
    .map(_ => println("ActorSystem terminated"))
}

case class SpecialMessage(contents: String)
case class SendMessageToYourself(content: String)
case class SayHiTo(ref: ActorRef)

class SimpleActor extends Actor {
  override def receive: Receive = {
    case "Hi" => sender() ! "Hello there"
    case message: String => println(s"[$self] I have received $message")
    case number: Int => println(s"[$self] I have received a number: $number")
    case SpecialMessage(content) => println(s"[$self] I have received special message: $content")
    case SendMessageToYourself(content) => self ! content
    case SayHiTo(ref) => ref ! "Hi"
  }
}