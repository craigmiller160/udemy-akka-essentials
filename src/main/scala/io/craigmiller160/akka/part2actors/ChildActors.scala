package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorRef, Props}
import io.craigmiller160.akka.utils.ActorSystemHandler

object ChildActors extends App {
  ActorSystemHandler.useSimpleSystem("childActors", system => {
    val parent = system.actorOf(Props[Parent], "parent")

    parent ! Parent.CreateChild("Bobby")
    parent ! Parent.TellChild("Hello World")
    parent ! Parent.TellChild("Hello Universe")

    val childSelection = system.actorSelection("/user/parent/Bobby")
    childSelection ! "I found you"
  })
}

object Parent {
  case class CreateChild(name: String)
  case class TellChild(message: String)
}
class Parent extends Actor {
  import Parent._
  override def receive: Receive = {
    case CreateChild(name) =>
      println(s"${self.path} = Creating child $name")
      val childRef = context.actorOf(Props[Child], name)
      context.become(withChild(childRef))
  }

  private def withChild(child: ActorRef): Receive = {
    case TellChild(message) =>
      child forward message
  }
}

class Child extends Actor {
  override def receive: Receive = {
    case message => println(s"${self.path} = $message")
  }
}