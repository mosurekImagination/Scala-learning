package http.highlevel

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

import java.io.File
//directives
import akka.http.scaladsl.server.Directives._

import scala.language.postfixOps

object UploadingFiles extends App {


  implicit val system: ActorSystem = ActorSystem("system")
  implicit val materializer: SystemMaterializer = SystemMaterializer(system)

  import system.dispatcher

  implicit val defaultTimeout: Timeout = Timeout(3 seconds)

  private val filesRoute: Route = (pathEndOrSingleSlash & get) {
      complete(
        HttpEntity(ContentTypes.`text/html(UTF-8)`,
        """
            |<html>
            |<body>
            |<form action="http://localhost:8000/upload" method="post" enctype="multipart/form-data">
            |<input type="file" name="myFile">
            |<button type="submit">Upload</button>
            |</form>
            |</body>
            |</html>
            |""".stripMargin)
      )
    } ~ (path("upload") & extractLog & post) { log =>
        //handle uploading files
        //multipart/form-data
        entity(as[Multipart.FormData]) { formdata =>
          //handle file payload
          val partsSource: Source[FormData.BodyPart, Any] = formdata.parts

          val filePartsSink: Sink[FormData.BodyPart, Future[Done]] = Sink.foreach[Multipart.FormData.BodyPart] { bodyPart =>
            if (bodyPart.name == "myFile") {
              // create a file
              val filepath = "src/main/resources/download/" + bodyPart.filename.getOrElse("tempFile_" + System.currentTimeMillis())
              val file = new File(filepath)
              log.info(s"Writing to file ${filepath}")

              val fileContentsSource = bodyPart.entity.dataBytes
              val fileContentSink = FileIO.toPath(file.toPath)
              fileContentsSource.runWith(fileContentSink)
            }
          }

          val writeOperationFuture = partsSource.runWith(filePartsSink)
          onComplete(writeOperationFuture) {
            case Success(_) => complete("File uploaded")
            case Failure(ex) => complete(s"File upload failed ${ex}")
          }
        }
      }

    Http()
    .newServerAt("localhost", 8000)
    .bind(filesRoute)
}
