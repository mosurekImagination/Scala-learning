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

object RequestLevelApi extends App with PaymentJsonProtocol {


  implicit val system: ActorSystem = ActorSystem("system")
  implicit val materializer: SystemMaterializer = SystemMaterializer(system)

  import system.dispatcher

  implicit val defaultTimeout: Timeout = Timeout(3 seconds)

  //freedom from managing anything
  //dead simple
  val responseFuture = Http().singleRequest(HttpRequest(uri = "http://www.google.com"))
  responseFuture.onComplete {
    case Success(value) =>
      value.discardEntityBytes()
      println(s"Response was successful: $value")
    case Failure(exception) => "There was an issue with request"
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
      uri = "http://localhost:8000/api/payments",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        paymentRequest.toJson.prettyPrint
      )
    )
  )

  Source(serverHttpRequests)
    .mapAsync(10)(request => Http().singleRequest(request)) // maintain order
    //.mapAsyncUnordered(10)(request => Http().singleRequest(request)) // do not maintain order
    .runForeach (println)
}