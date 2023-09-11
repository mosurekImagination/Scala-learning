package akka.clustered

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberJoined, MemberUp, UnreachableMember}
import com.typesafe.config.ConfigFactory

class ClusterSubscriber extends Actor with ActorLogging{
  val cluster = Cluster(context.system)
  //cluster.joinSeedNodes()
  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent],
      classOf[UnreachableMember],
    )
  }


  override def receive: Receive = {
    case MemberJoined(member) =>
      log.info(s"New member in town ${member.address}")
    case MemberUp(member)=>
      log.info(s"Welcome in the clusteR: ${member.address}")
      //.. etc
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }
}
object ClusteringBasics extends App {
  def startCluster(ports: List[Int]) = ports.foreach { port =>
    val config = ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port=${port}
         |""".stripMargin).withFallback(ConfigFactory.load("clustering/clusteringBasics.conf"))

    val system = ActorSystem("BasicCluster", config) // all actor systems in a cluster needs to be the same
    system.actorOf(Props[ClusterSubscriber](), "subscriber")
  }

  startCluster(List(2551, 2552, 0)) //0 means akka will alocate random port
}
