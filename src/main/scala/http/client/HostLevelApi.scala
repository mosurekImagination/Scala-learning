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
import java.util.UUID
//directives
import akka.http.scaladsl.server.Directives._

import scala.language.postfixOps

object HostLevelApi extends App with PaymentJsonProtocol {


  implicit val system: ActorSystem = ActorSystem("system")
  implicit val materializer: SystemMaterializer = SystemMaterializer(system)

  import system.dispatcher

  implicit val defaultTimeout: Timeout = Timeout(3 seconds)

  //high volume, low latency requests
  //pool connections
  // order of requests is not guaranteed
  val poolFlow = Http().cachedHostConnectionPool[Int]("www.google.com")

  Source(1 to 10)
    // we can pass additional data to the request, so in response we can fetch it and do something with it if necessary
    .map(i => (HttpRequest(), i))
    .via(poolFlow)
    .map {
      case (Success(response), value) =>
        response.discardEntityBytes() // it's important to free the connection! In other case you will have leaking connections!
        s"Request $value has received response $response"
      case (Failure(ex), value) =>
        s"Request $value has failed: $ex"
    }
    .runWith(Sink.foreach[String](println))


  val creditCards = List(
    CreditCard("1234-1234-1234-1234", "123"),
    CreditCard("1234-1234-1234-1235", "123"),
    CreditCard("1234-1234-1234-1236", "123")
  )
  val paymentRequests = creditCards.map(creditCard => PaymentRequest(creditCard, "someAccount", 99))
  val serverHttpRequests = paymentRequests.map(paymentRequest =>
    (HttpRequest(
      HttpMethods.POST,
      uri = Uri("/api/payments"),
      entity = HttpEntity(
        ContentTypes.`application/json`,
        paymentRequest.toJson.prettyPrint
      )
    ), UUID.randomUUID().toString)
  )

  Source(serverHttpRequests)
    .via(Http().cachedHostConnectionPool[String]("localhost", 8000))
    .runForeach{
      case (Success(response@HttpResponse(StatusCodes.Forbidden,_,_,_)), id) => println(s"orderId: $id was not allowed to proceed")
      case (Success(response), id) => println(s"orderId: $id was successful, $response")
      case (Failure(response), id) => println(s"order: $id didnt complete")
    }
}