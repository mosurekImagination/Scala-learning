package akka.introduction.playground

import akka.actor.ActorSystem

case object Playground extends App{

  val actorSystem = ActorSystem("HelloAkka")
  println(actorSystem.name)
}
