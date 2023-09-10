package akka.patten

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash}

object Stashing extends App {

  case object Open
  case object Close
  case object Read
  case class Write(data:String)

  class ResourceActor extends Actor with ActorLogging with Stash{
    //stash is like side-mailbox where we can store messages which can be processed later
    private  var innerData: String = ""
    override def receive: Receive = closed

    def closed: Receive = {
      case Open =>
        log.info("Opening")
        unstashAll()
        context.become(open)
      case message =>
        log.info(s"Stashing message: $message as I'm in closed state")
        stash()
    }
    def open: Receive = {
      case Close =>
        log.info("Closing")
        unstashAll()
        context.become(closed)
      case Read =>
        log.info(s"Reading - ${innerData}")
      case Write(message) =>
        log.info("Writing")
        innerData = message
    }
  }

  private val system = ActorSystem("system")
  val actor = system.actorOf(Props[ResourceActor](), "resourceActor")

  actor ! Write("test")
  actor ! Read
  actor ! Open

  //be carefull of
  // potential memory bounds on stash
  // potential mailbox bounds when unstashing
  // no stashing twice
  // the stash trait overrides preRestart so must be mixed-in last
}
