package http.highlevel


import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.{IncomingConnection, lookup}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.http.scaladsl.server.Route
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

import scala.language.postfixOps

object HighLevelDirectives extends App {


  implicit val system: ActorSystem = ActorSystem("system")
  implicit val materializer: SystemMaterializer = SystemMaterializer(system)

  import system.dispatcher

  //directives
  import akka.http.scaladsl.server.Directives._

  val simplePath: Route = path("home") { //directive - filters requests and executes another directive
    complete(StatusCodes.OK) //directive
  }

  // ROUTING TREE
  val chainedPath = path("chain") {
    get {
      complete(StatusCodes.OK)
    } ~ // very important - in other case due to scala syntax only the latest statement is taken into consideration
      post {
        complete(StatusCodes.OK)
      }
  } ~ simplePath

  val server = Http()
    .newServerAt("localhost", 8000)
    .bind(chainedPath)

  // Type Route = RequestContext => Future[RouteResult]
  // RequestContext is a data structure which wraps HttpRequest and other stuff
  // in most context you never need to build a RequestContext by yourself

  // Route can complete
  // synchronously, asynchronously with a Future, asynchronously with Source, reject and pass to the next Route, fail


  val tree =
    post {
      complete(StatusCodes.Forbidden)
    } ~
      path("about") {
        complete(
          HttpEntity(
            ContentTypes.`application/json`,
            """ "a": "b" """
          )
        )
      }
      //complex path
      ~ path("api" / "myEndpoint") {
      complete(StatusCodes.Forbidden)
    }
      ~ path("api/myEndpoint") { //this path is URL encoded so %2 instead of /
      complete(StatusCodes.Forbidden)
    }
      ~ pathEndOrSingleSlash { // localhost:8000 or localhost:8000/
      complete(StatusCodes.Forbidden)
    }
      ~ path("api" / "item" / IntNumber / IntNumber) { (id, inventory) =>
      complete(StatusCodes.Forbidden)
    }
      ~ path("api" / "item") {
      parameter(Symbol("id").as[String]) { itemId => // `id is a Symbol - it is kept in special JVM memory and it is always compared by reference (performance improvement)
        complete(StatusCodes.Forbidden)
      }
        ~ path("endpoint") {
        extractRequest { request =>
          complete(StatusCodes.Forbidden)
        }
      }
    } ~
      //composite directive of 2 filtering directive
      (path("api" / "item") & get) {
        complete(StatusCodes.Forbidden)
      } ~
      (path("api" / "item") & post & extractRequest & extractLog) { (request, log) =>
        complete(StatusCodes.Forbidden)
      } ~
      (path("about") | path("about2")) {
        complete(StatusCodes.Forbidden)
      } ~

    //actionable directives

    path("about") {
      complete(StatusCodes.Forbidden)
      failWith(RuntimeException("not supported")) // completes with HTTP 500
      reject // goes further in route tree
      chainedPath // goes directly to another route
    }
}
