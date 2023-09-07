package advanced.excercise

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.concurrent.duration.*


object Futures extends App {

  //immediate future
  val future = Future {
    5
  }
  println(future)

  // execute futures in Sequence

  val bFuture = Future {
    println("Started Future b")
    Thread.sleep(1000)
    println("Finished Future b")
    "b"
  }

  val aFuture = Future {
    println("Started Future a")
    Thread.sleep(1000)
    println("Finished Future a")
    "a"
  }
  //  println("going into for")
  //  for {
  //    a <- aFuture
  //    b <- bFuture
  //  } b

  //  println("second way")
  //  aFuture.andThen {
  //    case Success(r) => bFuture
  //  }


  // first value of 2 futures
//  val promise = Promise[String]
//
//  aFuture.onComplete {
//    case Success(r) => promise.trySuccess(r)
//  }
//  bFuture.onComplete {
//    case Success(r) => promise.trySuccess(r)
//  }
//  promise.future.onComplete(result => println(s"Result of 2 promises is ${result}"))

  // last value of 2 futures
  var bothPromise = Promise[String]
  var lastPromise = Promise[String]

  aFuture.onComplete { r=>
    if(!bothPromise.tryComplete(r))
      lastPromise.complete(r)
  }
  bFuture.onComplete { r =>
    if (!bothPromise.tryComplete(r))
      lastPromise.complete(r)
  }
  println(s"Result of 2 promises is ${lastPromise}")

  val someFuture = Future.successful(5)
  Thread.sleep(100)
  someFuture.onComplete(println)

  def retryUntil[A](action: () => Future[A], condition: A => Boolean): Future[A] =
    action()
      .filter(condition)
      .recoverWith{
        case _ => retryUntil(action, condition)
      }

}
