package akka.persistance

object PersistAsync extends App {

  //there is additional command available in Actors
  // persistAsync - which will not stash upcoming messages until callback is ran so we can process others messages in meantime

  // persistAsync still maintain order of callbacks
  // persistAsync increase performance -> high-throughput environments
}
