package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorRef, Props}
import io.craigmiller160.akka.utils.ActorSystemHandler

object ChildActors extends App {
  ActorSystemHandler.useSimpleSystem("childActors", system => {

  })
}

object Parent {
  case class CreateChild(name: String)
  case class TellChild(message: String)
}
class Parent extends Actor {
  import Parent._
  private var child: Option[ActorRef] = None

  override def receive: Receive = {
    case CreateChild(name) =>
      println(s"${self.path} = Creating child $name")
      val childRef = context.actorOf(Props[Child])
      child = Some(childRef)
    case TellChild(message) =>
      child.foreach(ref => ref ! message)
  }
}

class Child extends Actor {
  override def receive: Receive = {
    case message => println(s"${self.path} = $message")
  }
}