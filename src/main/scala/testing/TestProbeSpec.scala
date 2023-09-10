package testing

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.actors.ActorIntro2.SimpleActor
import akka.testkit.TestActors.{BlackholeActor, EchoActor}
import akka.testkit.{CallingThreadDispatcher, EventFilter, ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.*
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}

import concurrent.duration.DurationInt
import scala.language.postfixOps

class TestProbeSpec extends TestKit(ActorSystem("Spec"))
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with ImplicitSender //testActor
{
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  private val slave = TestProbe("slave")
  val slaveRef: ActorRef = slave.ref
  slave.expectMsg("test")
  slave.reply("asdf")
  slave.receiveWhile() {
    case "something" => slave.reply("replyOnSomething")
  }

  within(500 millis, 1 second) {
    slave.ref ! "DoSomething"
  }
  // we can asset also logs when we add proper intercepting configuration to akka system
  EventFilter.info(pattern = "Order [0-10]") intercept {
    // our test code
  }

  val sequence = receiveWhile(max = 2 seconds, idle = 500 millis, messages = 10) {
    case message => "5"
  }

  //synchronous testing
  val testActorRef2 = TestActorRef[EchoActor](Props[EchoActor]())
  testActorRef2 ! "some message" // actor already received message as // sending message to testActorRef happens in the same (calling) thread
  //at this point actor already got and processed the message
  testActorRef2.underlyingActor //.property == 1 // we can get properties
  testActorRef2.receive("directly pass object")

  val testActorRef3 = TestActorRef[EchoActor](Props[EchoActor]().withDispatcher(CallingThreadDispatcher.Id)) //calling thread dispatchers

  object TestProbeSpec {

  }
}
