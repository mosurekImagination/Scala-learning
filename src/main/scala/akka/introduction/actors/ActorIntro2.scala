package akka.introduction.actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorIntro2 extends App {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case "Hello" => context.sender() ! s"Hello There"
      case message: String => println(s"${self.path} - I have received $message")
      case message: Number => println(s"I have received number message $message")
      case SendMessageToOurSelf(content) => {
        println("Sending message to myself")
        self ! content
      }
//      case SayHi(reference) => reference ! "Hello"
      case SayHi(reference) => reference forward "Hello" // forwarding - keeping original sender
    }
  }

  val system = ActorSystem("System")

  private val simpleActor: ActorRef = system.actorOf(Props[SimpleActor](), "SimpleActor")
  simpleActor ! "Some message"

  //messages can be of any type
  // - messages must be IMMUTABLE
  // - messages must be SERIALIZABLE

  // in practive we use case classes and objects as messages
  simpleActor ! 42

  // actors have information about their context
  abstract class ExampleActor extends Actor {
    context.system
    context.self
    context.self.path
  }

  // we can even send messages to ourselfs!
  private case class SendMessageToOurSelf(val message: String)

  simpleActor ! SendMessageToOurSelf("Some message")


  //REPLIES of Actors

  val alice = system.actorOf(Props[SimpleActor](), "alice")
  val bob = system.actorOf(Props[SimpleActor](), "bob")

  case class SayHi(actorRef: ActorRef)

  alice ! SayHi(bob)

  // in case we reply to non existing actor it is going to "dead letters"
}
