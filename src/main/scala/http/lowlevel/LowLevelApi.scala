package http.lowlevel

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.IncomingConnection
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.stream.scaladsl.{Flow, Sink}
import akka.stream.{ActorMaterializer, SystemMaterializer}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object LowLevelApi extends App {

  implicit val system: ActorSystem = ActorSystem("system")
  implicit val materializer: SystemMaterializer = SystemMaterializer(system)

  import system.dispatcher

  val serverSource = Http()
    .newServerAt("localhost", 8001)
    .connectionSource()
  val connectionSink = Sink.foreach[IncomingConnection] { connection =>
    println(s"Accepted connection from: ${connection.remoteAddress}")
  }
  val serverBindingFuture = serverSource.to(connectionSink).run()
  serverBindingFuture.onComplete {
    case Success(value) => println("Server binding successful")
    case Failure(exception) => println(s"There was an error during server binding, ${exception}")
  }

  //method 1: synchronously server HTTP response
  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(HttpMethods.GET, uri, value, entity, protocol) =>
      HttpResponse(
        StatusCodes.OK, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, string =
          """
            |<html>
            |<body>
            |<h1> Hello World </h1>
            |</body>
            |</hmtl>
            |""".stripMargin)
      )

      // if there is no default response - it is interpreted as backpreassure
    case request: HttpRequest =>
      request.discardEntityBytes()
      HttpResponse(
        StatusCodes.NotFound, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, string =
          """
            |<html>
            |<body>
            |<h1>404 Not Found </h1>
            |</body>
            |</hmtl>
            |""".stripMargin)
      )
  }

  val httpSyncConnectionHandler = Sink.foreach[IncomingConnection] { connection =>
    connection.handleWithSyncHandler(requestHandler)
  }
  //  val serverSource2 = Http()
  //    .newServerAt("localhost", 8001)
  //    .connectionSource()
  //    .runWith(httpSyncConnectionHandler)

  //  val serverSource3 = Http()
  //    .newServerAt("localhost", 8001).bindSync(httpSyncConnectionHandler)

  //asynchronous
  // make sure you use your own execution context instead of akka.system.dispatcher to not starve akka system
  val requestAsyncHandler: HttpRequest => Future[HttpResponse] = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/home"), value, entity, protocol) =>
      Future(HttpResponse(
        StatusCodes.OK, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, string =
          """
            |<html>
            |<body>
            |<h1> Hello World </h1>
            |</body>
            |</hmtl>
            |""".stripMargin)
      ))
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
  val httpAsyncConnectionHandler = Sink.foreach[IncomingConnection] { connection =>
    connection.handleWithAsyncHandler(requestAsyncHandler)
  }

//  val serverSource3 = Http()
//    .newServerAt("localhost", 8000)
//    .connectionSource()
//    .runWith(httpAsyncConnectionHandler)


  // method 3: async via Akka streams
  val requestAsyncFlowHandler: Flow[HttpRequest, HttpResponse, _] = Flow[HttpRequest].map {
    case HttpRequest(HttpMethods.GET, Uri.Path("/home"), value, entity, protocol) =>
      HttpResponse(
        StatusCodes.OK, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, string =
          """
            |<html>
            |<body>
            |<h1> Hello World </h1>
            |</body>
            |</hmtl>
            |""".stripMargin)
      )
    case request: HttpRequest =>
      request.discardEntityBytes()
      HttpResponse(
        StatusCodes.NotFound, entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, string =
          """
            |<html>
            |<body>
            |<h1>404 Not Found </h1>
            |</body>
            |</hmtl>
            |""".stripMargin)
      )
  }

  val serverSource3 = Http()
      .newServerAt("localhost", 8000)
      .connectionSource()
      .runForeach{
        connection =>
          connection.handleWith(requestAsyncFlowHandler)
      }
}
