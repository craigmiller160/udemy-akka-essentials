package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import akka.actor.{Actor, ActorSystem, Props}

import scala.concurrent.ExecutionContext.Implicits.global

object ActorsIntro extends App {
  val actorSystem = ActorSystem("firstActorSystem")

  val wordCounter = actorSystem.actorOf(Props[WordCountActor], "wordCounter")
  val person = actorSystem.actorOf(Person.props("Bob"), "person")

  wordCounter ! "Hello World"
  wordCounter ! "Hello World 2"
  wordCounter ! "Hello World 3"
  wordCounter ! "Hello World 4"
  wordCounter ! "Hello World 5"
  wordCounter ! "Hello World 6"

  person ! "Hi"

  actorSystem.terminate()
    .map(_ => println("ActorSystem terminated"))
}

class WordCountActor extends Actor {
  println("[word counter] Instantiated")

  var totalWords = 0
  override def receive: Receive = {
    case message: String => {
      println(s"[word counter] I received message: $message")
      totalWords += message.split(" ").length
    }
    case msg => println(s"[word counter] I cannot understand ${msg.toString}")
  }
}

object Person {
  def props(name: String): Props = Props(new Person(name))
}
class Person(name: String) extends Actor {
  override def receive: Receive = {
    case "Hi" => println(s"Hi, my name is $name")
  }
}