package testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.actors.ActorIntro2.SimpleActor
import akka.testkit.TestActors.{BlackholeActor, EchoActor}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.*
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}
import concurrent.duration.DurationInt

import scala.language.postfixOps

class BasicSpec extends TestKit(ActorSystem("Spec"))
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with ImplicitSender //testActor
{

  //testactor is sent implicityly to each actor call
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A simple actor" should {
    "send back the same message" in {
      val echoActor = system.actorOf(Props[EchoActor](), "EchoActor")
      val message = "hello, test"
      echoActor ! message
      expectMsg(message)
    }
  }

  "A Blackhole actor" should {
    "shouldn't send message back" in {
      val echoActor = system.actorOf(Props[BlackholeActor](), "BlackholeActor")
      val message = "hello, test"
      echoActor ! message
      testActor
      expectNoMessage(2 second)
    }
  }


  "Test some assertions" should {
    "assert" in {
      expectMsg("")
      expectNoMessage()
      expectMsgAnyOf("a", "b")
      receiveN(2) //expect 2 messages

//      expectMsgPF{
//        case "a" => 1
//        case "b"=> 2
//      }
    }
  }

  object BasicSpec {
    class EchoActor extends Actor {
      override def receive: Receive = {
        case message => sender() ! message
      }
    }

    class Blackhole extends Actor {
      override def receive: Receive = {
        case message =>
      }
    }
  }
}
