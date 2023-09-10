package akka.persistance

import akka.actor.{ActorLogging, ActorRef, ActorSystem, Props}
import akka.persistance.RecoveryDemo.RecoveryDemo.Command
import akka.persistence.{PersistentActor, Recovery, RecoveryCompleted, SnapshotSelectionCriteria}

object RecoveryDemo extends App {

  //ALL COMMANDS SENT DURING RECOVERY ARE STASHED

  //If there is an error during recovery actor is stopped as it might be in inconsistent state
  object RecoveryDemo {
    case class Command(contents: String)

    case class Event(contents: String)
  }

  class RecoveryDemoActor extends PersistentActor with ActorLogging {
    import RecoveryDemo._

    //DO NOT persist more events after a customized recovery
    override def recovery: Recovery = Recovery(toSequenceNr = 100, fromSnapshot = SnapshotSelectionCriteria.Latest)
    override def receiveRecover: Receive = {
      case RecoveryCompleted => log.info("Recovery completed")
      case Event(contents) =>
        log.info(s"Recovered ${contents}")
        //log.info(s"Recovery status ${this.recoveryFinished}")

    }

    override def receiveCommand: Receive = {
      case Command(content) => persist(Event(content)) {
        e => log.info(s"Successfully persisted: ${e}")
      }
    }

    override def persistenceId: String = "recovery-demo"

    override def onRecoveryFailure(cause: Throwable, event: Option[Any]): Unit = {
      log.error(s" Catched error during recovery $cause")
      super.onRecoveryFailure(cause, event)
    }
  }

  val system = ActorSystem("system")
  private val actor: ActorRef = system.actorOf(Props[RecoveryDemoActor]())

  for(i <- 1 to 1000)
    actor ! Command(s"$i")
}


