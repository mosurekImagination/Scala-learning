package akka.distributed

import akka.actor.{Actor, ActorLogging, Props}

class SimpleActor extends Actor with ActorLogging{
    override def receive: Receive = {
    case m => log.info(s"Received message: $m from ${sender()}")
  }
}

object SimpleActor {
  def props = Props[SimpleActor]()
}
