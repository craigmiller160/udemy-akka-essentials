package io.craigmiller160.akka
package io.craigmiller160.akka.part2actors

import io.craigmiller160.akka.utils.ActorSystemHandler

import akka.actor.{Actor, ActorRef, Props}

object VotingSystemExercise extends App{
  ActorSystemHandler.useSimpleSystem("votingSystem", system => {
    val c1 = system.actorOf(Props[Citizen])
    val c2 = system.actorOf(Props[Citizen])
    val c3 = system.actorOf(Props[Citizen])
    val c4 = system.actorOf(Props[Citizen])
    val c5 = system.actorOf(Props[Citizen])
    val aggregator = system.actorOf(Props[VoteAggregator])

    val citizens = Set(c1, c2, c3, c4, c5)

    c1 ! Vote(Candidate.BILL_CLINTON)
    c2 ! Vote(Candidate.BILL_CLINTON)
    c3 ! Vote(Candidate.ROSS_PEROT)
    c4 ! Vote(Candidate.GEORGE_BUSH)

    aggregator ! AggregateVotes(citizens)
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
case object AggregationComplete

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
  override def receive: Receive = awaitAggregation

  private def awaitAggregation: Receive = {
    case AggregateVotes(citizens) =>
      citizens.foreach(ref => ref ! VoteStatusRequest)
      context.become(doAggregate(VoteAggregation(0, 0, 0), citizens.size))
  }

  private def doAggregate(aggregation: VoteAggregation, votesToCount: Int): Receive = {
    case VoteStatusReply(candidate) =>
      val newAggregation = candidate match {
        case Some(Candidate.BILL_CLINTON) => aggregation.copy(billClinton = aggregation.billClinton + 1)
        case Some(Candidate.GEORGE_BUSH) => aggregation.copy(georgeBush = aggregation.georgeBush + 1)
        case Some(Candidate.ROSS_PEROT) => aggregation.copy(rossPerot = aggregation.rossPerot + 1)
        case None => aggregation
      }
      val newVotesToCount = votesToCount - 1
      if (newVotesToCount > 0) {
        context.become(doAggregate(newAggregation, newVotesToCount))
      } else {
        self ! AggregationComplete
      }
    case AggregationComplete =>
      printVotes(aggregation)
      context.become(awaitAggregation)
  }

  private def printVotes(aggregation: VoteAggregation): Unit = {
    println("VOTES:")
    println(s"  Bill Clinton: ${aggregation.billClinton}")
    println(s"  Ross Perot: ${aggregation.rossPerot}")
    println(s"  George Bush: ${aggregation.georgeBush}")
  }
}