package http.highlevel

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.{IncomingConnection, lookup}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.http.scaladsl.server.{Directive, ExceptionHandler, MethodRejection, MissingQueryParamRejection, Rejection, RejectionHandler, Route}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, SystemMaterializer}
import spray.json.*

import scala.concurrent.duration.*
import scala.concurrent.Future
import scala.util.{Failure, Success}
import akka.stream.SystemMaterializer
import http.lowlevel.GuitarDb.{CreateGuitar, FindAllGuitars, FindGuitarById, GuitarCreated}
import akka.pattern.ask
import akka.util.{CompactByteString, Timeout}
import http.lowlevel.{Guitar, GuitarDb, GuitarStoreJsonProtocol}
//directives
import akka.http.scaladsl.server.Directives._

import scala.language.postfixOps

object WebSocketsDemo extends App {


  implicit val system: ActorSystem = ActorSystem("system")
  implicit val materializer: SystemMaterializer = SystemMaterializer(system)

  import system.dispatcher

  implicit val defaultTimeout: Timeout = Timeout(3 seconds)

  //Message: TextMessage vs BinaryMessage

  val textMessage = TextMessage("Something")
  val textMessageStream = TextMessage(Source.single("Something"))
  val binaryMessage = BinaryMessage(Source.single(CompactByteString("Something")))

  def websocketFlow: Flow[Message, Message, Any] = Flow[Message].map {
    case message: TextMessage =>
      TextMessage(Source.single("Server says back") ++ message.textStream ++ Source.single("!"))
    case message: BinaryMessage =>
      message.dataStream.runWith(Sink.ignore)
      TextMessage(Source.single("Server received binary message"))
  }

  val websocketRoute =
    (pathEndOrSingleSlash & get) {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html))
    } ~
      path("greeter") {
        handleWebSocketMessages(socialFlow)
      }

  val serverSource3 = Http()
    .newServerAt("localhost", 8000)
    .connectionSource()
    .runForeach {
      connection =>
        connection.handleWithAsyncHandler(websocketRoute)
    }
}

case class SocialPost(owner:String, content: String)
val socialFeed = Source(
  List(
    SocialPost("Tom", "It's great"),
    SocialPost("Mat", "Yea, I agree completely"),
    SocialPost("Player", "Who wants to play?")
  )
)
val socialMessages = socialFeed
  .throttle(1, 2 seconds)
  .map(socialPost => TextMessage(s"${socialPost.owner} said: ${socialPost.content}"))
val socialFlow: Flow[Message, Message, Any] = Flow.fromSinkAndSource(
  Sink.foreach[Message](println),
  socialMessages
)
val html =
  """
    |<html>
    |<head>
    |    <script>
    |        var exampleSocket = new WebSocket('ws://localhost:8000/greeter');
    |        console.log("starting websocket");
    |
    |        exampleSocket.onmessage = function(event){
    |            var newChild = document.createElement("div");
    |            newChild.innerText = event.data
    |            document.getElementById("1").appendChild(newChild);
    |        }
    |
    |        exampleSocket.onopen = function(event){
    |            exampleSocket.send("socket seems to be open...")
    |        }
    |        exampleSocket.send("socket says: hello")
    |
    |    </script>
    |</head>
    |<body>
    |Starting websocket
    |<div id="1"></div>
    |</body>
    |</html>
    |""".stripMargin