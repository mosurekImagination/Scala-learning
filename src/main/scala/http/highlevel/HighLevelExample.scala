package http.highlevel

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.{IncomingConnection, lookup}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.http.scaladsl.server.{Directive, Route}
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

object HighLevelExample extends App with GuitarStoreJsonProtocol {


  implicit val system: ActorSystem = ActorSystem("system")
  implicit val materializer: SystemMaterializer = SystemMaterializer(system)

  import system.dispatcher

  implicit val defaultTimeout: Timeout = Timeout(3 seconds)
  import GuitarDb._

  val guitarDb = system.actorOf(Props[GuitarDb]())
  List(
    Guitar("brand", "name"),
    Guitar("brand1", "name1"),
    Guitar("brand2", "name2"),
  ).foreach {
    guitarDb ! CreateGuitar(_)
  }

  val guitarServerRoute =
    path("api" / "guitar") {
      parameter(Symbol("id").as[Int]) { id =>
        // more specific endpoints first!
        get {
          val response = (guitarDb ? FindGuitarById(id))
            .mapTo[Option[Guitar]]
            .map { guitar =>
              HttpEntity(ContentTypes.`application/json`, guitar.toJson.prettyPrint)
            }
          complete(response)
        }
      } ~
        get {
          val guitars = guitarDb ? FindAllGuitars
          val responseFuture = guitars
            .mapTo[List[Guitar]]
            .map(guitars =>
              HttpEntity(ContentTypes.`application/json`, guitars.toJson.prettyPrint)
            )
          complete(responseFuture)
        }
    }
      ~ path("api" / "guitar" / IntNumber) { id =>
      get {
        val response = (guitarDb ? FindGuitarById(id))
          .mapTo[Option[Guitar]]
          .map { guitar =>
            HttpEntity(ContentTypes.`application/json`, guitar.toJson.prettyPrint)
          }
        complete(response)
      }
    }

  //requests is going through route tree and it is getting matched or rejected. Rejected != failed. Rejections are aggregated
  val serverSource3 = Http()
    .newServerAt("localhost", 8000)
    .connectionSource()
    .runForeach {
      connection =>
        connection.handleWithAsyncHandler(guitarServerRoute)
    }

  def toHttpEntity(payload: String) = HttpEntity(ContentTypes.`application/json`, payload)

  val simplifiedRoute =
    (pathPrefix("api" / "guitar") & get) {
      (path(IntNumber) | parameter(Symbol("id").as[Int])) { id =>
        complete((guitarDb ? FindGuitarById(id))
          .mapTo[Option[Guitar]]
          .map(_.toJson.prettyPrint)
          .map(toHttpEntity)
        )
      } ~
        pathEndOrSingleSlash {
          complete((guitarDb ? FindAllGuitars)
            .mapTo[List[Guitar]]
            .map(_.toJson.prettyPrint)
            .map(toHttpEntity)
          )
        }
    }
}
