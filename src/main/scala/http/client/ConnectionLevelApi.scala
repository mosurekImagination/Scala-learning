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
import http.client.PaymentSystemDomain.PaymentRequest

import java.io.File
//directives
import akka.http.scaladsl.server.Directives._

import scala.language.postfixOps

object ConnectionLevelApi extends App with PaymentJsonProtocol {

  //useful when we want to send lots of request to the same server as we open connection once
  implicit val system: ActorSystem = ActorSystem("system")
  implicit val materializer: SystemMaterializer = SystemMaterializer(system)

  import system.dispatcher

  implicit val defaultTimeout: Timeout = Timeout(3 seconds)

  val connectionFlow = Http().outgoingConnection("www.google.com")

  def oneOffRequest(request: HttpRequest) = Source.single(request).via(connectionFlow).runWith(Sink.head)

  oneOffRequest(HttpRequest()).onComplete {
    case Success(value) => println(s"Got successful response: ${value}")
    case Failure(exception) => println(s"Exception: ${exception}")
  }

  val creditCards = List(
    CreditCard("1234-1234-1234-1234", "123"),
    CreditCard("1234-1234-1234-1235", "123"),
    CreditCard("1234-1234-1234-1236", "123")
  )
  val paymentRequests = creditCards.map(creditCard => PaymentRequest(creditCard, "someAccount", 99))
    val serverHttpRequests = paymentRequests.map(paymentRequest =>
      HttpRequest(
        HttpMethods.POST,
        uri = Uri("/api/payments"),
        entity = HttpEntity(
          ContentTypes.`application/json`,
          paymentRequest.toJson.prettyPrint
        )
      )
    )

    Source(serverHttpRequests)
      .via(Http().outgoingConnection("localhost", 8000))
      .to(Sink.foreach[HttpResponse](println))
      .run()
}
