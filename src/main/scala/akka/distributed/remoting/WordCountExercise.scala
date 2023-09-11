package akka.distributed

import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, ActorSystem, Identify, PoisonPill, Props}
import akka.distributed.MasterApp.actorSystem
import akka.introduction.actors.ChildActor.WordCounterMaster.WordCountTask
import com.typesafe.config.ConfigFactory

object WordCountDomain {
  case class Initialize(nWorkers: Int)

  case class WorkCountTask(text: String)

  case class WordCountResult(count: Int)

  case object EndWordCount
}

class WordCountWorker extends Actor with ActorLogging {

  import WordCountDomain._

  override def receive: Receive = {
    case WorkCountTask(text) =>
      log.info(s"Processing ${text}")
      sender() ! WordCountResult(text.split(" ").length)
  }
}

class WordCountMaster extends Actor with ActorLogging {

  import WordCountDomain._

  override def receive: Receive = {
    case Initialize(nWorkers) =>
      log.info(s"Initializing ${nWorkers} in remote JVM")
      val workersSelection = (1 to nWorkers).map(id => context.actorSelection(s"akka://WorkerSystem@localhost:2552/user/Worker-${id}"))
      workersSelection.foreach(_ ! Identify("someKey"))
      context.become(initializing(List(), nWorkers))
  }

  def initializing(list: List[ActorRef], remainingWorkers: Int): Receive = {
    case ActorIdentity("someKey", Some(actorRef)) =>
      if (remainingWorkers == 1) context.become(online(actorRef :: list, 0, 0))
      else context.become(initializing(actorRef :: list, remainingWorkers - 1))
  }

  def online(workers: List[ActorRef], remainingTasks: Int, totalCount: Int): Receive = {
    case text: String =>
      val sentences = text.split("\\.")
      Iterator.continually(workers).flatten.zip(sentences.iterator).foreach { pair =>
        val (worker, sentence) = pair
        worker ! WordCountTask(sentence)
      }
      context.become(online(workers, remainingTasks + sentences.length, totalCount))
    case WordCountResult(count) =>
      if (remainingTasks == 1) {
        log.info(s"TOTAL RESULT IS: ${totalCount + count}")
        workers.foreach(_ ! PoisonPill)
        context.stop(self)
      } else {
        context.become(online(workers, remainingTasks - 1, totalCount + count))
      }

  }

}

object MasterApp extends App {

  import WordCountDomain._

  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2551
      |""".stripMargin
  ).withFallback(ConfigFactory.load("distributed/remoteActorsExercise.conf"))

  val actorSystem = ActorSystem("MasterSystem")
  val master = actorSystem.actorOf(Props[WordCountMaster](), "MasterActor")

  master ! Initialize(5)

  val fileSource = scala.io.Source.fromFile("src/main/resources/bigFile.txt")
  fileSource.getLines().foreach { line =>
    master ! line
  }

  fileSource.close()

}

object WorkersApp extends App {

  import WordCountDomain._

  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2552
      |""".stripMargin
  ).withFallback(ConfigFactory.load("distributed/remoteActorsExercise.conf"))
  val actorSystem = ActorSystem("WorkersSystem")
  (1 to 5).foreach(i => actorSystem.actorOf(Props[WordCountWorker](), s"Worker-${i}"))

}
