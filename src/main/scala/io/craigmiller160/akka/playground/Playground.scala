package io.craigmiller160.akka
package io.craigmiller160.akka.playground

import akka.actor.ActorSystem

object Playground extends App {
  val actorSystem = ActorSystem("HelloAkka")
  println(actorSystem.name)
}
