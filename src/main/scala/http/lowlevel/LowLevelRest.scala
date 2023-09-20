package http.lowlevel

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.{IncomingConnection, lookup}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
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

case class Guitar(make: String, model: String)

object GuitarDb {
  case class CreateGuitar(guitar: Guitar)

  case class GuitarCreated(id: Int)

  case class FindGuitarById(id: Int)

  case object FindAllGuitars
}

trait GuitarStoreJsonProtocol extends DefaultJsonProtocol {
  implicit val guitarFormat: RootJsonFormat[Guitar] = jsonFormat2(Guitar)
}

class GuitarDb extends Actor with ActorLogging {

  import GuitarDb._

  var guitars: Map[Int, Guitar] = Map()
  var currentGuitarId: Int = 0

  override def receive: Receive = {
    case FindAllGuitars =>
      log.info("Searching for all guitars")
      sender() ! guitars.values.toList
    case FindGuitarById(id) =>
      log.info(s"Searching guitar by id: ${id}")
      sender() ! guitars.get(id)
    case CreateGuitar(guitar: Guitar) =>
      log.info(s"Creating guitar ${guitar}")
      guitars = guitars + (currentGuitarId -> guitar)
      sender() ! GuitarCreated(currentGuitarId)
      currentGuitarId += 1
  }
}

object LowLevelRest extends App
  with GuitarStoreJsonProtocol // bringing into the scope
{


  implicit val system: ActorSystem = ActorSystem("system")
  implicit val materializer: SystemMaterializer = SystemMaterializer(system)

  import system.dispatcher

  val guitarDb = system.actorOf(Props[GuitarDb]())
  List(
    Guitar("brand", "name"),
    Guitar("brand1", "name1"),
    Guitar("brand2", "name2"),
  ).foreach {
    guitarDb ! CreateGuitar(_)
  }

  implicit val defaultTimeout: Timeout = Timeout(3 seconds)

  val simpleGuitar = Guitar("Fender", "Stratocaster")
  //marshalling
  private val guitarJson = simpleGuitar.toJson.prettyPrint
  //unmarshalling
  println(guitarJson.parseJson.convertTo[Guitar])
  val requestHandler: HttpRequest => Future[HttpResponse] = {
    case HttpRequest(HttpMethods.GET, uri@Uri.Path("/api/guitar"), value, entity, protocol) if uri.query().isEmpty =>
      val guitarsFuture: Future[List[Guitar]] = (guitarDb ? FindAllGuitars).mapTo[List[Guitar]]
      guitarsFuture.map { guitars =>
        HttpResponse(
          entity = HttpEntity(
            ContentTypes.`application/json`,
            guitars.toJson.prettyPrint
          )
        )
      }
    case HttpRequest(HttpMethods.GET, uri@Uri.Path("/api/guitar"), value, entity, protocol) =>
      val guitarId = uri.query().get("id").map(_.toInt)
      guitarId match
        case None => Future(HttpResponse(StatusCodes.NotFound))
        case Some(id) =>
          val guitarFuture = (guitarDb ? FindGuitarById(id)).mapTo[Option[Guitar]]
          guitarFuture.map {
            case None => HttpResponse(StatusCodes.NotFound)
            case Some(guitar) =>
              HttpResponse(
                entity = HttpEntity(
                  ContentTypes.`application/json`,
                  guitar.toJson.prettyPrint
                )
              )
          }
    case HttpRequest(HttpMethods.POST, Uri.Path("/api/guitar"), value, entity, protocol) =>
      //entities are a Source[ByteString]
      val strictEntity = entity.toStrict(3 seconds)
      strictEntity.flatMap { strictEntity =>
        val parsedGuitar = strictEntity.data.utf8String.parseJson.convertTo[Guitar]

        val guitarCreatedFuture = (guitarDb ? CreateGuitar(parsedGuitar)).mapTo[GuitarCreated]
        guitarCreatedFuture.map { guitarCreated =>
          HttpResponse(StatusCodes.OK)
        }
      }

    // if there is no default response - it is interpreted as backpreassure
    case request: HttpRequest =>
      request.discardEntityBytes()
      Future(HttpResponse(
        StatusCodes.NotFound, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, string =
          """
            |<html>
            |<body>
            |<h1>404 Not Found </h1>
            |</body>
            |</hmtl>
            |""".stripMargin)
      )
      )
  }

  //Marshalling is transforming the data to the format client can understand
  val serverSource3 = Http()
    .newServerAt("localhost", 8000)
    .connectionSource()
    .runForeach {
      connection =>
        connection.handleWithAsyncHandler(requestHandler)
    }

}
