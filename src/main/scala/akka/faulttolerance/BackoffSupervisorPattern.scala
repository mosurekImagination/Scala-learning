package akka.faulttolerance

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props}
import akka.pattern.{Backoff, BackoffSupervisor}

import java.io.File
import scala.io.Source
import scala.concurrent.duration.*
import scala.language.postfixOps

object BackoffSupervisorPattern extends App {

  case object ReadFile

  class FileBasedPersistentActor extends Actor with ActorLogging {

    var dataSource: Source = null

    override def preStart(): Unit = log.info("PersistentActor starting")

    override def postStop(): Unit = log.info("PersistentActor stopping")

    override def preRestart(reason: Throwable, message: Option[Any]): Unit = log.info("PersistentActor restarting")

    override def receive: Receive = {
      case ReadFile => if (dataSource == null)
        dataSource = Source.fromFile(new File("src/main/resources/test_2.txt"))
        log.info("I just read test file: \n" + dataSource.getLines().toList)
    }
  }

  val system = ActorSystem("actorSystem")
  val actor = system.actorOf(Props[FileBasedPersistentActor](), "actor")
  actor ! ReadFile

  val simpleSupervisorProps = BackoffSupervisor.props(
    // it can be onFailure, onStop
    Backoff.onFailure(
      Props[FileBasedPersistentActor](),
      "simpleBackoffActor",
      3 seconds,
      30 seconds,
      0.2
    ).withSupervisorStrategy{
      OneForOneStrategy() {
        case _ => Stop
      }
    },
  )
  // here we are creating 2 actors
  // simple supervisor actor which forwards all messages to our actor
  // has simpleBackOff actor as a child
  // in case of restart
  // - supervision strategy is the default one (restarting on everything)
  // - first attempt after 3 seconds
  // - next attempt is 2x the previous attempt
  val backoffActor = system.actorOf(simpleSupervisorProps, "simpleSupervisor")

  backoffActor ! ReadFile

  class EagerPersistentActor extends Actor with ActorLogging {

    var dataSource: Source = Source.fromFile(new File("src/main/resources/test_2.txt"))

    override def preStart(): Unit = log.info("EagerActor starting")

    override def postStop(): Unit = log.info("EagerActor stopping")

    override def preRestart(reason: Throwable, message: Option[Any]): Unit = log.info("EagerActor restarting")

    override def receive: Receive = {
      case ReadFile => if (dataSource == null)
        dataSource = Source.fromFile(new File("src/main/resources/test_2.txt"))
        log.info("I just read test file: \n" + dataSource.getLines().toList)
    }
  }

  val supervisorProps = BackoffSupervisor.props(
    // it can be onFailure, onStop
    Backoff.onStop(
      Props[EagerPersistentActor](),
      "eagerActor",
      3 seconds,
      30 seconds,
      0.2
    ),
  )

  val eagerActorSupervisor = system.actorOf(supervisorProps, "eagerActorSupervisor")
  //ActorInitializationException => STOP instead of restart by default


  eagerActorSupervisor ! ReadFile
}
