package akka.introduction.actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App {

  //part 1 - actor system
  val actorSystem = ActorSystem("firstActorSystem")
  println(actorSystem.name)

  //part 2 - actors
  //actors are uniquely identified
  // messages are asynchronous
  // each actor may respond differently
  // actors are encapsulated

  class WordCountActor extends Actor {
    //internal data
    var totalWords = 0

    //handler (behaviour)
    def receive: Receive = {
      case message: String => {
        println("I have received message")
        totalWords += message.split(" ").length
      }
      case illegalMessage => println(s"I cannot understand message ${illegalMessage}")
    }
  }

  class Person(name: String) extends Actor {
    override def receive: Receive = {
      case _ => "Something"
    }
  }

  // part 3 - instantiate our actor
  private val wordCounter = actorSystem.actorOf(Props[WordCountActor](), "wordCounter")

  // part 4 - communicate
  wordCounter ! "I am learning Akka"
  //asynchronous
  // ! - method called "tell"

  val person = actorSystem.actorOf(Props(Person("Bob"))) // instantiation of Actor inside Props is possible but is is not encouraged!

  // this is recommended way
  //create companion object which based on some parameters creates Props with that object
  object Person {
    def props(name: String) = Props(Person(name))
  }

  val secondPerson = actorSystem.actorOf(Person.props("Bob2"))
}
