package intro.learn.functional

import scala.util.{Failure, Random, Success, Try}

object FailureHandling extends App {

  //Try is a wrapper for computations that might Fail or Succeed
  val success = Success("Success")
  val failure = Failure(new RuntimeException("Failure"))

  def unsafeMethod(): String = throw new RuntimeException("No string")

  val result = Try(unsafeMethod()) //doesn't blow up a program.
  println(result)

  //syntax sugar
  val potentialFailure = Try {
    unsafeMethod()
  }
  potentialFailure.isFailure
  potentialFailure.isSuccess
  val fallback = potentialFailure.orElse(Try("asdf"))

  //if your API might throw exception, wrap it with Try
  def betterUnsafeMethod(): Try[String] = Failure(
    throw new RuntimeException("No string"))

  def betterFallbackMethod(): Try[String] = Success("Fallback")

  betterUnsafeMethod() orElse betterFallbackMethod()

  //Try has also map, flatmap and filter method implemented
  // so we can use for comprehension


  /*
    Exercise
   */
  val host = "localhost"
  val port = "8080"

  def renderHTML(page: String) = println(page)

  class Connection {
    def get(url: String): String = {
      val random = new Random(System.nanoTime())
      if (random.nextBoolean()) "<html>...</html>"
      else throw new RuntimeException("Connection interrupted")
    }

    def getSafe(url: String): Try[String] = Try(get(url))
  }

  object HttpService {
    val random = new Random(System.nanoTime())

    def getConnection(host: String, port: String): Connection =
      if (random.nextBoolean()) new Connection
      else throw new RuntimeException("Someone else took the port")

    def getSafeConnection(host: String, port: String): Try[Connection] = Try(getConnection(host, port))
  }

  // if you get the html page from the connection, print it to the console i.e. call renderHTML
  val possibleConnection = HttpService.getSafeConnection(host, port)
  val possibleHTML = possibleConnection.flatMap(connection => connection.getSafe("/home"))
  possibleHTML.foreach(renderHTML)

  // shorthand version
  HttpService.getSafeConnection(host, port)
    .flatMap(connection => connection.getSafe("/home"))
    .foreach(renderHTML)

  // for-comprehension version

  /*
    try {
      connection = HttpService.getConnection(host, port)
      try {
        page = connection.get("/home")
        renderHTML(page)
      } catch (some other exception) {

      }
    } catch (exception) {

    }
   */

}
