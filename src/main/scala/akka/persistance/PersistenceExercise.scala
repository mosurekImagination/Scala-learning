package akka.persistance

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.introduction.playground.ActorExercise.Vote
import akka.persistance.PersistenceExercise.PersistenceExercise.{Result, VoteCommand}
import akka.persistence.PersistentActor

object PersistenceExercise extends App{

  //keep citizens who voted
  //pool mapping between candidate and the number of received votes so far
  object PersistenceExercise {
    case object Result
    case class VoteCommand(citizenId: String, candidate: String)
    case class VoteEvent(citizenId: String, candidate: String)
  }

  class VotingActor extends PersistentActor with ActorLogging{
    import PersistenceExercise._
    override def persistenceId: String = "voting-actor"
    var poll: Map[String, Int] = Map()

    override def receiveCommand: Receive = {
      case vote @ VoteCommand(citizenId, candidate) =>
        log.info(s"Received for for: $citizenId -> $candidate")
        val currentVote = poll.getOrElse(candidate, 0)
        persist(VoteEvent(citizenId, candidate)){ e =>
          poll = poll + (candidate -> (currentVote + 1))
        }
      case Result => log.info(poll.toString())

    }

    override def receiveRecover: Receive =
      case VoteEvent(citizenId, candidate) =>
        log.info(s"Recover for for: $citizenId -> $candidate")
        val currentVote = poll.getOrElse(candidate, 0)
        poll = poll + (candidate -> (currentVote + 1))
  }

  val system = ActorSystem("system")
  val actor = system.actorOf(Props[VotingActor]())
//  actor ! VoteCommand("1", "a")
//  actor ! VoteCommand("2", "b")
//  actor ! VoteCommand("3", "c")
//  actor ! VoteCommand("4", "a")
  actor ! Result
}
