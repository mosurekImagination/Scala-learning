package akka.introduction.infra

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}
import akka.introduction.infra.Schedulers.EchoActor
import Schedulers.EchoActor
import akka.routing.{ActorRefRoutee, Broadcast, FromConfig, RoundRobinGroup, RoundRobinPool, RoundRobinRoutingLogic, Router}


object Routers extends App {

  class Master extends Actor{
    private val slaves: List[ActorRef] = List()

    //round-robin
    //random
    //smallest mailbox
    //broadcast - all
    //scatter-gather-first
    //tail-chopping
    //consistent-hashing
    private var router = Router(RoundRobinRoutingLogic(), slaves.map(ActorRefRoutee).toIndexedSeq)
    override def receive: Receive = {
      case message => router.route(message, sender())
      case Terminated(ref) =>
        router = router.removeRoutee(ref)
        val newSlave = context.actorOf(Props[EchoActor](), "simpleActor")
        router =  router.addRoutee(newSlave)
        context.watch(newSlave)
    }
  }

  //Pool Master
  private val system = ActorSystem("system")
  val poolMaster = system.actorOf(RoundRobinPool(5).props(Props[EchoActor]()), "simplePoolMaster")
  (1 to 10).foreach(i => poolMaster ! "someMessage")

  //from config configuration
  //val configMaster = system.actorOf(FromConfig.props(Props[EchoActor](), "poolMaster2"))

  //Directly provide actors to routing
  //val directMaster = system.actorOf(RoundRobinGroup(List(poolMaster).map(_.path.toString)).props(), "directlyProvidedMaster")

  //BROADCASTING
  poolMaster ! Broadcast("toEveryone")

}
