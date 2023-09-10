package akka.persistance.patterns

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object PersistenceQueryApp extends App {

  //queries
    // select persistence IDs
    // select events by persistence ID
    // select events across persistence IDS, by tags

    val system = ActorSystem("")

  implicit val materializer: ActorMaterializer = ActorMaterializer()(system)
  //val journal = PersistenceQuery(system).readJurnalFor///
  // you can add tags by Tagged(event, tags*) method - doesnt guarantee correct order as it could be saved in multiple nodes
}
