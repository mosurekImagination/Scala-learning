package akka.introduction.infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Timers}

import scala.concurrent.duration.*
import scala.language.postfixOps

object Schedulers extends App {

  class EchoActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("system")
  val simpleActor = system.actorOf(Props[EchoActor](), "simpleActor")
  system.log.info("Scheduling remainder for simpleActor")

  import system.dispatcher
  system.scheduler.scheduleOnce(3 second){
    simpleActor ! "Some message"
  }
  val schedule = system.scheduler.scheduleWithFixedDelay(3 second, 1 seconds){
    () => simpleActor ! "Heartbeat"
  }
  system.scheduler.scheduleOnce(5 second){
    schedule.cancel()
  }
  //(system.dispatcher)

  //all scheduled tasks execute when the system is terminated
  //schedulers are not the best at precision and long-term planning

  class TimerBasedSelfClosingActor extends Actor with Timers{
    timers.startSingleTimer("timerKey", "start", 500 millis)
    override def receive: Receive = {
      case "start" =>
        println("Bootstrapping")
        //we override timers in case we have the same key
        timers.startTimerWithFixedDelay("timerKey", "remainder", 1 seconds)
      case "remainder" => println("remainder loop")
    }
  }

  val simpleActor2 = system.actorOf(Props[TimerBasedSelfClosingActor](), "simpleActor2")

}
