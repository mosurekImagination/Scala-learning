package akka.actors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.actors.ChildActor.WordCounterMaster.{Initialize, WordCountReply, WordCountTask}
import akka.event.Logging

object ChildActor extends App {

  //LOGGING
  //logging is asynchronous, implemented via Actors
  // you can change logger, e.g. SLF4J
  
  //we can create actor inside actor
  // that will add actor name inside with actor parent path
  // we have child hierarchy

  // we have system guardians (top-level)
  // /system = system guardian
  // /user = user-level guardian
  // / = root guardian -> manages system and user guardians


  private val system = ActorSystem("system")

  //actor selection
  val childSelection = system.actorSelection("/user/parent/child")

  //Danger
  //Never pass mutable actor state or the 'this' reference to child actors
  //don't break actor encapsulation!
  // instead of passing JVM real objects, pass ActorRef which are managing these entities
  // all communication must go through messages

  //distributed word counting
  // round robin logic
  object WordCounterMaster {
    case class Initialize(nChildren: Int)

    case class WordCountTask(text: String)

    case class WordCountReply(text: String, count: Int)
  }

  class WordCounterMaster extends Actor {
    override def receive: Receive = initialize
    val logger = Logging(context.system, this)

    def initialize: Receive = {
      case Initialize(childrens) =>
        val children = (1 to childrens).map(i => context.actorOf(Props[WordCounterWorker](), i.toString)).toSet
        context.become(withChildren(Iterator.continually(children).flatten))

    }
    def withChildren(child: Iterator[ActorRef]): Receive = {
      case WordCountTask(text) =>
        val nextWorker = child.next()
        logger.info(s"Sending work to the next worker which is ${nextWorker.path}")
        nextWorker ! WordCountTask(text)
      case WordCountReply(text, count) =>
        logger.info(s"Received response from worker: ${sender().path}, count of ${text} is ${count}")
    }
  }

  private class WordCounterWorker extends Actor with ActorLogging{
    override def receive: Receive = {
      case WordCountTask(text) =>
        log.info(s"Working on: ${text}")
        Thread.sleep(500)
        sender() ! WordCountReply(text, text.split(" ").length)
    }
  }

  val master = system.actorOf(Props[WordCounterMaster](), "Master")

  master ! Initialize(3)
  master ! WordCountTask("1 2 3")
  master ! WordCountTask("1 2 3 4 5")
  master ! WordCountTask("1 2 3 4 5 6 7")
  master ! WordCountTask("1 2 3 4 5 6 7 8 9")
  master ! WordCountTask("1 2 3 4 5 6 7 8 9 10")

}
