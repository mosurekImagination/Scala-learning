package akka.patten

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Cancellable, FSM, Props}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, OneInstancePerTest}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.{ExecutionContext, duration}
import concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}
import akka.pattern.pipe

class FiniteStateMachine extends TestKit(ActorSystem("Spec"))
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with ImplicitSender
  with OneInstancePerTest // will create test kit for each test // in other case schedules don't work properly
   {

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  object FiniteStateMachine {
    case class Initialize(inventory: Map[String, Int], prices: Map[String, Int])

    case class RequestProduct(product: String)

    case class Instruction(instruction: String)

    case class Deliver(product: String)

    case class GiveBackChange(amount: Int)

    case class VendingError(reason: String)

    case object ReceiveMoneyTimeout

    trait VendingState

    case object Idle extends VendingState

    case object Operational extends VendingState

    case object WaitForMoney extends VendingState

    trait VendingData

    case object Uninitialized extends VendingData

    case class Initialized(inventory: Map[String, Int], prices: Map[String, Int]) extends VendingData

    case class ReceiveMoney(amount: Int) extends VendingData

    case class WaitForMoneyData(
                                 inventory: Map[String, Int], prices: Map[String, Int], product: String, money: Int,
                                 //moneyTimeoutSchedule: Cancellable,
                                 requester: ActorRef
                               ) extends VendingData

    class VendingMachineFiniteStateMachine extends FSM[VendingState, VendingData] with ActorLogging {
      // we don't message messages directly
      // event(message, data)
      // we handle STATES and EVENTS instead of messages

      startWith(Idle, Uninitialized)
      //equivalent of
      //override def receive:Receive = idle (handler change to the first one)

      //FSM is an agent which has a state (instance of a class) and data (instance of a class)
      // event => state and data can be changed

      when(Idle) {
        case Event(Initialize(inventory, prices), Uninitialized) => goto(Operational) using Initialized(inventory, prices)
        //equivalent of context.become(operational(inventory,prices))
        case _ =>
          sender() ! VendingError("Machine not initialized error")
          stay()
      }

      when(Operational) {
        case Event(RequestProduct(product), Initialized(inventory, prices)) =>
          inventory.get(product) match {
            case None | Some(0) =>
              sender() ! VendingError("ProductNotAvailable")
              stay()
            case Some(_) =>
              val price = prices(product)
              sender() ! Instruction(s"Please insert ${price} dollars")
              goto(WaitForMoney) using WaitForMoneyData(inventory, prices, product, 0, sender())
          }
      }
      when(WaitForMoney, stateTimeout = 1 second) {
        case Event(StateTimeout, WaitForMoneyData(inventory, prices, product, money, requester)) =>
          requester ! VendingError("RequestTimeOut")
          if (money > 0) requester ! GiveBackChange(money)
          goto(Operational) using Initialized(inventory, prices)
        case Event(ReceiveMoney(amount), WaitForMoneyData(inventory, prices, product, money, requester)) =>
          val price = prices(product)
          val newBalance = money + amount
          if (newBalance >= price) {
            requester ! Deliver(product)
            if (newBalance - price > 0) requester ! GiveBackChange(newBalance - price)

            val newStock = inventory(product) - 1
            val newInventory = inventory + (product -> newStock)
            goto(Operational) using Initialized(newInventory, prices)
          }
          else {
            val remainingMoney = price - money - amount
            requester ! Instruction(s"Please insert ${remainingMoney}")
            stay() using WaitForMoneyData(inventory, prices, product, newBalance, requester)
          }
      }

      whenUnhandled {
        case _ =>
          sender() ! VendingError("Command not found")
          stay()
      }

      onTransition {
        case stateA -> stateB => log.info(s"Transitioning from ${stateA} to ${stateB}")
      }

      // REMEMBER to initialize !
      initialize()
    }
  }
}