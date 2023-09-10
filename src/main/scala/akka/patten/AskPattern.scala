package akka.patten

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.{ExecutionContext, duration}
import concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}
import akka.pattern.pipe
class AskPattern extends TestKit(ActorSystem("Spec"))
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with ImplicitSender {

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "An authenticator" should {
    "fail to authenticate a non-registered user" in {
      val authManager = system.actorOf({
        Props(AuthManager())
      })
      authManager ! Authenticate("tom", "password")
      expectMsg(AuthFailure("username not found"))
    }
    "fail to authenticate with invalid password" in {
      val authManager = system.actorOf({
        Props(AuthManager())
      })
      authManager ! RegisterUser("tom", "password")
      authManager ! Authenticate("tom", "wrongPassword1234")
      expectMsg(AuthFailure("wrong password"))
    }
    "successfully authenticate" in {
      val authManager = system.actorOf({
        Props(AuthManager())
      })
      authManager ! RegisterUser("tom", "password")
      authManager ! Authenticate("tom", "password")
      expectMsg(AuthSuccess)
    }
  }
  "Piped authenticator" should {
    "fail to authenticate a non-registered user" in {
      val authManager = system.actorOf({
        Props(PipedAuthManager())
      })
      authManager ! Authenticate("tom", "password")
      expectMsg(AuthFailure("username not found"))
    }
    "fail to authenticate with invalid password" in {
      val authManager = system.actorOf({
        Props(PipedAuthManager())
      })
      authManager ! RegisterUser("tom", "password")
      authManager ! Authenticate("tom", "wrongPassword1234")
      expectMsg(AuthFailure("wrong password"))
    }
    "successfully authenticate" in {
      val authManager = system.actorOf({
        Props(PipedAuthManager())
      })
      authManager ! RegisterUser("tom", "password")
      authManager ! Authenticate("tom", "password")
      expectMsg(AuthSuccess)
    }
  }

  object AskSpec {
    case class Read(key: String)

    case class Write(key: String, value: String)
  }

  import AskSpec._

  class KeyValueActor extends Actor with ActorLogging {
    override def receive: Receive = online(Map())

    private def online(kv: Map[String, String]): Receive = {
      case Read(key) =>
        log.info(s"Trying to read key: ${key}")
        sender() ! kv.get(key)
      case Write(key, value) =>
        log.info(s"Writing value: ${value} to key: ${key}")
        context.become(online(kv + (key -> value)))
    }
  }

  case class RegisterUser(username: String, password: String)

  case class Authenticate(username: String, password: String)

  case class AuthFailure(message: String)

  case object AuthSuccess

  class AuthManager extends Actor with ActorLogging {
    val authDb = context.actorOf({
      Props(KeyValueActor())
    })

    //logistics
    implicit val timeout: Timeout = Timeout(1 second)
    implicit val executionContext: ExecutionContext = context.dispatcher

    override def receive: Receive = {
      case RegisterUser(username, password) => authDb ! Write(username, password)
      case Authenticate(username, password) => handleAuthentication(username, password)
    }

    def handleAuthentication(username: String, password: String): Unit = {
      val originalSender = sender()
      val future = authDb ? Read(username)
      future.onComplete {
        // NEVER CALL METHODS ON THE ACTOR INSTANCE OR ACCESS MUTABLE STATE IN ONCOMPLETE!
        // in other case we risk closing-over the actor instance or mutable state!
        case Success(None) => originalSender ! AuthFailure("username not found")
        case Success(Some(dbPassword)) => if (password == dbPassword) originalSender ! AuthSuccess else originalSender ! AuthFailure("wrong password")
        case Failure(exception) => originalSender ! AuthFailure(s"Unexpected failure: ${exception}")
      }
    }
  }

  class PipedAuthManager extends AuthManager{

    override def handleAuthentication(username: String, password: String) = {
      val future = authDb ? Read(username) //future
      val mappedFuture = future.mapTo[Option[String]]
      val responseFuture = mappedFuture.map{
        case None => AuthFailure("username not found")
        case Some(dbPassword) => if (password == dbPassword) AuthSuccess else AuthFailure("wrong password")
      }
      //when the future completes, send the reponse to the actor ref in the arg list
      responseFuture.pipeTo(sender())
    }  }
}