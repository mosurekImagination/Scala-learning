package http.highlevel

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import spray.json.enrichAny

case class Book(id: Int, name: String)

trait BookJsonProtocol extends DefaultJsonProtocol {
  implicit val bookFormat: RootJsonFormat[Book] = jsonFormat2(Book.apply)
}

class RouteDSLSpec extends AnyWordSpecLike with Matchers with ScalatestRouteTest with BookJsonProtocol {

  import RouteDSLSpec._

  "A digital library beckend" should {
    "return all the books in the library" in {
      Get("/api/book") ~> libraryRoute ~> check {
        status shouldBe StatusCodes.OK
        entityAs[List[Book]] shouldBe books
      }
    }
    "return book with specified id" in {
      Get("/api/book/1") ~> libraryRoute ~> check {
        status shouldBe StatusCodes.OK
        entityAs[Book] shouldBe books.head
      }
    }
    "not accept other methods than POST and GET" in {
      Delete("/api/books")~> libraryRoute ~> check {
      rejections should not be empty
      }
    }
  }
}

object RouteDSLSpec extends BookJsonProtocol with SprayJsonSupport {
  var books: Seq[Book] = List(
    Book(1, "1"),
    Book(2, "2"),
  )

  val libraryRoute = pathPrefix("api" / "book") {
    get {
      (path(IntNumber) | parameter(Symbol("id").as[Int])) { id =>
        complete(books.find(_.id == id))
      } ~
        pathEndOrSingleSlash {
          complete(HttpEntity(ContentTypes.`application/json`, books.toJson.prettyPrint))
        }
    } ~
      post {
        entity(as[Book]) { book =>
          books = books :+ book
          complete(StatusCodes.OK)
        }
        complete(StatusCodes.BadRequest)
      }
  }
}
