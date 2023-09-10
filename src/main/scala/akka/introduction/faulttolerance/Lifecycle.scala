package akka.introduction.faulttolerance

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, ActorSystem, AllForOneStrategy, Kill, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy}

import java.util.UUID
import scala.language.postfixOps
import concurrent.duration.DurationInt
import scala.concurrent.Future

object Lifecycle extends App {

  //started - create a new actorref with a uuid at a given path
  //suspended - the actor ref will enqueue but not process more messages
  //resumed - the actor ref will continue processing more messages
  //restarted
  //stopped

  //restarted (internat state is destroyed on restart)
  // suspend
  // swap the actor instance
  //  old instance calls preRestart
  //  replace actor instance
  //  new instance calls postRestart
  // resume

  //stopped
  // call postStop()
  // all watching actors receive Terminated(ref)
  // actor ref is released

  // in case of restart we first recreate parent and then child by default supervision strategy

  val system = ActorSystem("Lifecycle")
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  class Parent extends Actor {
    var children: List[ActorRef] = List()

    override def receive: Receive = {
      case "restart" => throw RuntimeException("exception")
      case "startChild" => children = children.appended(context.actorOf(Props[Child](), "child"))
      //context.stop is non-blocking method! it sends only a signal to stop child
      //child actors are stopped before parent one
      case "stopChildren" => children.foreach(children => context.stop(children))
        println("Send stop signal to children")
    }

    override def preStart(): Unit = println("Parent - I'm starting")

    override def postStop(): Unit = println("Parent - I'm stopping")

    override def preRestart(reason: Throwable, message: Option[Any]): Unit =
      println("Parent - pre restarting")
    override def postRestart(reason: Throwable): Unit =
      println("Parent - post restarting")
  }

  class Child extends Actor {
    override def receive: Receive = {
      //we can watch if actors are being terminated by watch command
      //case "watch" => context.watch(actorRef)
      // and then we receive terminated event
      // we will get messages even from already dead actors
      //case Terminated(ref) =>
      case message => println("Child actor action")
    }

    override def preStart(): Unit = println("Child - I'm starting")

    override def postStop(): Unit = println("Child - I'm stopping")


    override def preRestart(reason: Throwable, message: Option[Any]): Unit =
      println("Child - pre restarting")

    override def postRestart(reason: Throwable): Unit =
      println("Child - post restarting")
  }


  private val parent: ActorRef = system.actorOf(Props[Parent](), "parent")
  parent ! "startChild"
  //  parent ! "stopChildren"

  // you can also send message which PoisonKill and Kill
  parent ! PoisonPill // stop message processed in normal, sequential way
  //parent ! Kill // throws exception in actor - kills automatically once it reaches target actor


  private val parent2: ActorRef = system.actorOf(Props[Parent](), "parent2")
  parent2 ! "startChild"

  parent2 ! "restart"


  //Supervision strategies
  // It's fine if actors crash
  // parent must decide upon their children's failure

  // when actor fails it
  // - suspends its children
  // - sends a (special) message to its parent

  // the parent can decide to
  // - resume the actor
  // - restart the actor (default)
  // - stop  the actor
  // - escalate and fail itself

  class OneForOneStrategyActor extends Actor{
    override def receive: Receive = {
      case message => "something"
    }

    //actions applied only to the actor exception came
    override val supervisorStrategy = OneForOneStrategy(){
      case _: NullPointerException => SupervisorStrategy.Restart
      case _: IllegalArgumentException => SupervisorStrategy.Stop
      case _: Exception => SupervisorStrategy.Escalate
    }
  }

  class AllForOneStrategyActor extends Actor{
    override def receive: Receive = {
      case message => "something"
    }

    //actions applied to ALL child actors
    override val supervisorStrategy = AllForOneStrategy(){
      case _: NullPointerException => SupervisorStrategy.Restart
      case _: IllegalArgumentException => SupervisorStrategy.Stop
      case _: Exception => SupervisorStrategy.Escalate
    }
  }

}
