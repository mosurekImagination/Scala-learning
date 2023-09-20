package http.client

import akka.Done
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.{IncomingConnection, lookup}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.Multipart.FormData
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, Multipart, StatusCodes, Uri}
import akka.http.scaladsl.server.{Directive, ExceptionHandler, MethodRejection, MissingQueryParamRejection, Rejection, RejectionHandler, Route}
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source}
import akka.stream.{ActorMaterializer, SystemMaterializer}
import spray.json.*
import akka.http.scaladsl.server.Route.*
import akka.http.scaladsl.server.Route

import scala.concurrent.duration.*
import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.stream.SystemMaterializer
import akka.pattern.ask
import akka.util.{CompactByteString, Timeout}
import http.client.ConnectionLevelApi.system
import http.client.PaymentSystemDomain.PaymentRequest

import java.io.File
//directives
import akka.http.scaladsl.server.Directives._

import scala.language.postfixOps

case class CreditCard(serialNumber: String, securityCode: String)

object PaymentSystemDomain {
  case class PaymentRequest(creditCard: CreditCard, receiverAccount: String, amount: Double)

  case object PaymentAccepted

  case object PaymentRejected
}

trait PaymentJsonProtocol extends DefaultJsonProtocol {
  implicit val creditCardFormat: RootJsonFormat[CreditCard] = jsonFormat2(CreditCard.apply)
  implicit val paymentRequestFormat: RootJsonFormat[PaymentRequest] = jsonFormat3(PaymentRequest.apply)
}

class PaymentValidator extends Actor with ActorLogging {

  import PaymentSystemDomain._

  override def receive: Receive = {
    case PaymentRequest(CreditCard(serialNumber, _), receiverAccount, amount) =>
      log.info(s"$serialNumber is trying to send $amount dollars to $receiverAccount")
      if (serialNumber == "1234-1234-1234-1234")
        sender() ! PaymentRejected
      else sender() ! PaymentAccepted
  }
}

object PaymentSystem extends App
  with PaymentJsonProtocol
  with SprayJsonSupport {

  implicit val system: ActorSystem = ActorSystem("PaymentSystem")
  implicit val materializer: SystemMaterializer = SystemMaterializer(system)
  implicit val defaultTimeout: Timeout = Timeout(3 seconds)

  import system.dispatcher
  import PaymentSystemDomain._

  val paymentValidator = system.actorOf(Props[PaymentValidator]())

   private val paymentRoute: Route = path("api" / "payments") {
    post {
      entity(as[PaymentRequest]) { paymentRequest =>
        val validationResponse = (paymentValidator ? paymentRequest).map{
          case PaymentRejected => StatusCodes.Forbidden
          case PaymentAccepted => StatusCodes.OK
          case _ => StatusCodes.BadRequest
        }
        complete(validationResponse)
      }
    }
  }


  Http()
    .newServerAt("localhost", 8000)
    .bind(paymentRoute)
}
