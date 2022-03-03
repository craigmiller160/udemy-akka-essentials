package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import io.craigmiller160.akka.utils.ActorSystemHandler

import akka.actor.{Actor, ActorRef}

object VotingSystemExercise extends App{
  ActorSystemHandler.useSimpleSystem("votingSystem", system => {

  })
}

object Candidate extends Enumeration {
  type Candidate = Value

  val BILL_CLINTON, GEORGE_BUSH, ROSS_PEROT = Value
}
case class Vote(candidate: Candidate.Value)
case object VoteStatusRequest
case class VoteStatusReply(candidate: Option[Candidate.Value])
case class VoteAggregation(billClinton: Int, georgeBush: Int, rossPerot: Int)
case class AggregateVotes(citizens: Set[ActorRef])

class Citizen extends Actor {
  override def receive: Receive = ???
}
class VoteAggregator extends Actor {
  override def receive: Receive = ???
}