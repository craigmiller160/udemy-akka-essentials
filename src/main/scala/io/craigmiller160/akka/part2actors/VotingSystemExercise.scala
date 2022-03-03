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
  override def receive: Receive = doReceive(None)

  private def doReceive(voteChoice: Option[Candidate.Value]): Receive = {
    case Vote(candidate) => voteChoice match {
      case Some(_) => // Do nothing
      case None => context.become(doReceive(Some(candidate)))
    }
    case VoteStatusRequest => sender() ! VoteStatusReply(voteChoice)
  }
}
class VoteAggregator extends Actor {
  override def receive: Receive = doReceive(VoteAggregation(0, 0, 0))

  // TODO what if multiple AggregateVotes requests are sent?
  private def doReceive(aggregation: VoteAggregation): Receive = {
    case AggregateVotes(citizens) =>
      citizens.foreach(ref => ref ! VoteStatusRequest)
    case VoteStatusReply(candidate) => candidate match {
      case Candidate.BILL_CLINTON => context.become(doReceive(aggregation.copy(billClinton = aggregation.billClinton + 1)))
      case Candidate.GEORGE_BUSH => context.become(doReceive(aggregation.copy(georgeBush = aggregation.georgeBush + 1)))
      case Candidate.ROSS_PEROT => context.become(doReceive(aggregation.copy(rossPerot = aggregation.rossPerot + 1)))
    }
  }
}