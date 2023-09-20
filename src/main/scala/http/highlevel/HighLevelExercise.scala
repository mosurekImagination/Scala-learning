package http.highlevel

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.{IncomingConnection, lookup}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.http.scaladsl.server.{Directive, ExceptionHandler, MethodRejection, MissingQueryParamRejection, Rejection, RejectionHandler, Route}
import akka.stream.scaladsl.{Flow, Sink}
import akka.stream.{ActorMaterializer, SystemMaterializer}
import spray.json.*

import scala.concurrent.duration.*
import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.stream.SystemMaterializer
import http.lowlevel.GuitarDb.{CreateGuitar, FindAllGuitars, FindGuitarById, GuitarCreated}
import akka.pattern.ask
import akka.util.Timeout
import http.lowlevel.{Guitar, GuitarDb, GuitarStoreJsonProtocol}
//directives
import akka.http.scaladsl.server.Directives._

import scala.language.postfixOps

case class Person(pin: Int, name: String)

trait PersonJsonProtocol extends DefaultJsonProtocol {
  implicit val personFormat: RootJsonFormat[Person] = jsonFormat2(Person)
}

object HighLevelExercise extends App with PersonJsonProtocol
  with SprayJsonSupport //whatever can be converted to json, it can also be converted to Marsharable thing so we can pass it directly to complete
{


  implicit val system: ActorSystem = ActorSystem("system")
  implicit val materializer: SystemMaterializer = SystemMaterializer(system)

  import system.dispatcher

  implicit val defaultTimeout: Timeout = Timeout(3 seconds)

  def toHttpEntity(payload: String) = HttpEntity(ContentTypes.`application/json`, payload)

  var people = List(
    Person(1, "one"),
    Person(2, "two"),
    Person(3, "three"),
    Person(3, "four"),
  )
  val rejectionHandler: RejectionHandler = { (rejections: Seq[Rejection]) =>
    Some(complete(StatusCodes.BadRequest))
  }

  implicit val customRejectionHandler: RejectionHandler = RejectionHandler.newBuilder()
    .handle { //check list of rejections against this handle and moves futher to the next handler
      case m: MethodRejection =>
        complete("Rejected Method")
    }.handle {
      case m: MissingQueryParamRejection =>
        complete("Something")
    }.result()

  // if exception is not handled there, default one still is kicked in.
  val customExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: RuntimeException => complete(StatusCodes.NotFound, e.getMessage)
    case e: IllegalArgumentException => complete(StatusCodes.NotFound, e.getMessage)
  }

  val router: Route =
    handleExceptions(customExceptionHandler) { // by default there is 500 exception handler
      handleRejections(rejectionHandler) { // by default there is implicit 404 rejection handler
        pathPrefix("api" / "people") {
          get {
            (path(IntNumber) | parameter(Symbol("pin").as[Int])) { pin =>
              complete(
                toHttpEntity(people.filter(_.pin == pin).toJson.prettyPrint)
              )
            } ~
              pathEndOrSingleSlash {
                complete(
                  toHttpEntity(people.toJson.prettyPrint)
                )
              }
          } ~
            post {
              entity(as[Person]) { person =>
                people = people.appended(person)
                complete(StatusCodes.OK)
              }
            }
        }
      }
    }


  val serverSource3 = Http()
    .newServerAt("localhost", 8000)
    .connectionSource()
    .runForeach {
      connection =>
        connection.handleWithAsyncHandler(router)
    }
}
