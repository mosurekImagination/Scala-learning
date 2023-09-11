package akka.distributed

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorSystem, Identify, Props}
import akka.distributed.RemoteActors.remoteSystem
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.*
import scala.language.postfixOps
import scala.util.{Failure, Success}
object RemoteActors extends App {

  val localSystem = ActorSystem("LocalSystem", ConfigFactory.load("distributed/remoteActors.conf").atKey("secondRemoteSystem"))
  val remoteSystem = ActorSystem("RemoteSystem", ConfigFactory.load("distributed/remoteActors.conf"))

  val localSimpleActor = localSystem.actorOf(SimpleActor.props, "simpleLocalActor")
  val remoteSimpleActor = remoteSystem.actorOf(SimpleActor.props, "remoteSimpleActor")

  localSimpleActor ! "hello, local"
  remoteSimpleActor ! "hello, remote"

  // 1 actor selection
  val selection = localSystem.actorSelection("akka://RemoteSystem@localhost:2551/user/remoteSimpleActor")
  selection ! "hello fom different cluster"

  // 2 resolve actorRef
  import localSystem.dispatcher
  implicit val timeout: Timeout = Timeout(3 seconds)
  val remoteActorRefFuture = selection.resolveOne().onComplete {
    case Success(value) => value ! "Resolved actor"
    case Failure(exception) => println("Failed to resolve actor")
  }

  // 3 actor identification  via messages - recommendation
  class ActorResolver extends Actor with ActorLogging {
    override def preStart(): Unit = {
      val selection = context.actorSelection("akka://RemoteSystem@localhost:2551/user/remoteSimpleActor")
      selection ! Identify("something")
    }

    override def receive: Receive = {
      case ActorIdentity("something", Some(actorRef)) => actorRef ! "Thank you for identification"
      case a @ _ => log.info(s"Received different message from cluster: ${a}")
    }
  }

  val newActor = localSystem.actorOf(Props[ActorResolver]())
}

object RemoteActors_Remote extends App{
  val localSystem = ActorSystem("RemoteSystem", ConfigFactory.load("distributed/remoteActors.conf").atKey("secondRemoteSystem"))
  val remoteSimpleActor = localSystem.actorOf(SimpleActor.props, "remoteSimpleActor")

  val selection = localSystem.actorSelection("akka://RemoteSystem@localhost:25520/user/remoteSimpleActor")
  selection ! "hello from different cluster and JVM"

}