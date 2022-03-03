package io.craigmiller160.akka
package io.craigmiller160.akka.utils

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext.Implicits.global

object ActorSystemHandler {
  def useSimpleSystem(name: String, useSystem: ActorSystem => Unit): Unit = {
    val system = ActorSystem(name)
    useSystem(system)

    system.terminate()
      .map(_ => println("ActorSystem terminated"))
  }
}
