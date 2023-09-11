import akka.actor.{ActorSystem, Address, AddressFromURIString, Deploy, Props}
import akka.introduction.actors.ActorIntro2.SimpleActor
import akka.remote.RemoteScope

object DeployingActorsRemotely extends App {

  // the name of the actor is checked in config for remote deployment
  //  if it snot there, ti will be created locally
  //  the props passed to actorOf() will be sent to the remote actor system
  // the remote actor system will create it there
  // actorRef is returned

  // Props objects needs to be serializable! in 99% it is
  // lambda is not serializable
  // actor class neds to be in the remote JVM classpath

  //actual path of deployed remotely actors contains @remote@local/actor so we can see from which system actor comes

  private val system = ActorSystem("test")
  //programatic remote deployment
  val remoteSystemAddress: Address = AddressFromURIString("akka://RemoteSystem@localhost:2552")
  val remotelyDeployedActor = system.actorOf(Props[SimpleActor]().withDeploy(
    Deploy(scope = RemoteScope(remoteSystemAddress))
  ))


  // pool router automaticaly creates children and route messages

  // Failure Detector
  // The phi accrual failure detector
  //  actor systems send heartbeat messages once a connection is established
  //   - sending a message
  //   - deploying a remote actor
  //  if a heartbeat times out, its reach score (PHI) increases
  //  if the PHI score passes a threshold, the connection is quarantined -> unreachable
  // the local actor system sends Terminated messages to Death Watchers of remote actors
  //  the remote actor system must be restarted to reestablish connection
}
