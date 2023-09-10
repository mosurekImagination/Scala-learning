package akka.introduction.infra

import akka.actor.{ActorSystem, Props}
import akka.dispatch.{ControlMessage, PriorityGenerator, UnboundedPriorityMailbox}
import Schedulers.EchoActor
import com.typesafe.config.{Config, ConfigFactory}

object Dispatchers extends App {


  //text config is not present in resource hence it doesnt work
//  val system = ActorSystem("system", ConfigFactory.load().getConfig("dispatchersDemo"))
//  val someActor = system.actorOf(Props[EchoActor]().withDispatcher("my-dispatcher"))

  //thread-pool-executor
    //fixed-pool-size
  //throughput

  //Dispatcher, PinnedDispatcher, CallingThreadDispatcher
  //use dedicated dispatcher for blocking calls to not starve your actors!

  class PriorityMailbox(settings: ActorSystem.Settings, config: Config) extends UnboundedPriorityMailbox(
    PriorityGenerator {
      case "P0" => 0 // the most important one
      case _ => 1
    }
  )
  val system = ActorSystem("system")
  //that dispatcher also needs to come from config
  //val actor = system.actorOf(Props[EchoActor].withDispatcher())

  // we can increase priority with Control messages
  case object SomeImportantMessage extends ControlMessage

  //make the actor attach to the mailbox
  //val actor = system.actorOf(Props[EchoActor].withMailbox())

}
