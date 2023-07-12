package intro.learn.patternmatching

object EvenNextPatterns extends App {

  try {

  } catch {
    case e: RuntimeException => "Runtime"
    case _ => "Something lese"
  }

  //Try catch are Pattern matching!

  val list = List(1, 2, 3, 4)
  for {
    x <- list if x % 2 == 0
  } yield 10 * x

  // generators are also based on pattern matching!

  val tuples = List((1, 2), (3, 4))
  for {
    (a, b) <- tuples
  } yield a * b

  // deconstruction
  val (a, b, c) = (1, 2, 3)
  val head :: tail = List(1, 2)

  val mappedList = list.map {
    case x if x % 2 == 0 => 2 * x
    case 1 => 1
    case _ => -1
  }
  //this is the same
  val mappedList2 = list.map { x =>
    x match {
      case x if x % 2 == 0 => 2 * x
      case 1 => 1
      case _ => -1
    }
  }
}
