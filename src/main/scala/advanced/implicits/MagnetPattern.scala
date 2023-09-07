package advanced.implicits

import scala.concurrent.Future

object MagnetPattern extends App {

  // method overloading
  // 1. type erasure
  // 2. lifting doesn't work for all overloads
  // 3. code duplication
  // 4. type interference and default args

  trait Actor {
    def receive(future: Future[Int]): Int
//    def receive(future: Future[Long]): Int doesn't work due to type erasure
  }

  trait MessageMagnet[Result]{
    def apply(): Result
  }

  def receive[R](magnet: MessageMagnet[R]):R = magnet()

  implicit class FromIntegerRequest(request: Int) extends MessageMagnet[Int]{
    override def apply(): Int = 1
  }
  implicit class FromLongRequest(request: Long) extends MessageMagnet[Long]{
    override def apply(): Long = 2
  }

  receive(2L)

  //magnet pattern advantages
  // no more type erasure problems
  // we can have Future[Int] and also Future[Long]
  // uplifting converter function to higher order functions

  //magnet pattern drawback
  // verbose
  // harder to read
  // you can't name or place default arguments
  // call by name doesn't work correctly

}
