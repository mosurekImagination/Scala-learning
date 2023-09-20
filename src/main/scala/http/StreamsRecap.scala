package http

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object StreamsRecap extends App {

  implicit val system: ActorSystem = ActorSystem("StreamRecap")
  implicit val materializer: Materializer = Materializer(system)

  import system.dispatcher

  val source = Source(1 to 100)
  val sink = Sink.foreach[Int](println)
  val flow = Flow[Int].map(x => x + 1)
  val runnableGraph = source.via(flow).to(sink)
  //  val simpleMaterializedValue: NotUsed = runnableGraph.run() //materialization
  // not used materialized value

  // MATERIALIZED VALUE
  val sumSink = Sink.fold[Int, Int](0)((x, y) => x + y)
  val sumFuture: Future[Int] = source.runWith(sumSink)
  //here we materialize both source and sink and we choose sink materialization

  sumFuture.onComplete {
    case Success(value) => println(s"Sum is: $value")
    case Failure(exception) => println("There was an exception")
  }

  //  val anotherMaterializedValue = source
  //    .viaMat(flow)(Keep.right)
  //    .viaMat(sink)(Keep.left)
  //    .run()

  // materializing a graph means materializing ALL the components
  // a materialized value can be ANYTHING AT ALL!

  // Backpressure actions
  // buffer elements
  // apply a strategy in case the buffer overflows
  // you can fail the entire stream

  val bufferedFlow = Flow[Int].buffer(10, OverflowStrategy.dropHead)
  source
    .async
    .via(bufferedFlow) // backpressure signal
    .async
    .runForeach { e =>
      Thread.sleep(100)
      println(e)
    }
}
