package akka.introduction.playground

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import ActorExercise.Counter.{Decrement, Increment, Print}

object ActorExercise extends App {

  object Counter {
    case object Increment

    case object Decrement

    case object Print
  }

  val system = ActorSystem("CounterSystem")

  class Counter extends Actor {
    override def receive: Receive = countReceive(0)

    def countReceive(currentCount: Int): Receive = {
      case Increment => context.become(countReceive(currentCount + 1))
      case Decrement => context.become(countReceive(currentCount - 1))
      case Print => println(s"I'm ${self.path} - and my state is: ${currentCount}")
    }
  }

  val counterActor = system.actorOf(Props[Counter](), "CounterActor")
  counterActor ! Print
  counterActor ! Increment
  counterActor ! Print
  counterActor ! Increment
  counterActor ! Decrement
  counterActor ! Decrement
  counterActor ! Print

  case class Vote(candidate: String)

  case object VoteStatusRequest

  case class VoteStatusReply(candidate: Option[String])

  class Citizen extends Actor {
    override def receive: Receive = {
      case Vote(candidate) => context.become(voted(candidate))
      case VoteStatusRequest => sender() ! VoteStatusReply(None)
    }

    def voted(candidateName: String): Receive = {
      case VoteStatusRequest =>
        println(s"${self.path} - Responding that I voted for ${candidateName}")
        sender() ! VoteStatusReply(Some(candidateName))
    }
  }

  case class AggregateVotes(citizen: Set[ActorRef])

  class VoteAggregator extends Actor {
    override def receive: Receive = {
      case AggregateVotes(citizens) =>
        citizens.foreach(_ ! VoteStatusRequest)
        context.become(receiveWithState(citizens.size - 1, Map()))
    }

    def receiveWithState(remainingVotes: Int, votes: Map[String, Int]): Receive = {
      case VoteStatusReply(candidateName) =>
        println(s"Gathering vote for ${candidateName}")
        val votesMap = candidateName
          .map(name => votes.updatedWith(name)(option => Some(option.getOrElse(0) + 1)))
          .getOrElse(votes)
        if (remainingVotes == 0) println(votesMap) else
          println(s"Votes left: ${remainingVotes}, waiting")
        context.become(receiveWithState(remainingVotes - 1, votesMap))

    }
  }

  val citizen1 = system.actorOf(Props[Citizen](), "Citizen1")
  val citizen2 = system.actorOf(Props[Citizen](), "Citizen2")
  val citizen3 = system.actorOf(Props[Citizen](), "Citizen3")
  val citizen4 = system.actorOf(Props[Citizen](), "Citizen4")
  citizen1 ! Vote("A")
  citizen2 ! Vote("B")
  citizen3 ! Vote("C")
  citizen4 ! Vote("A")
  val voteAggregator = system.actorOf(Props[VoteAggregator](), "VoteAggregator")
  voteAggregator ! AggregateVotes(Set(citizen1, citizen2, citizen3, citizen4))


}
