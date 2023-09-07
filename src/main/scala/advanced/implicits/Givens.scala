package advanced.implicits

object Givens extends App {

  val aList = List(1, 4, 2, 3)
  val anOrderedList = aList.sorted

  //Scala 2 style
  object Implicits {
    implicit val descendingOrder: Ordering[Int] = Ordering.fromLessThan(_ > _)
  }

  //Scala 3 style
  object Implicits2 {
    given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

  }
  //in given order of instruction doesnt matter It will be picked up anyway
  println(anOrderedList)

  //inline anonymous class
  given descendingOrdering_v3: Ordering[Int] with {
    override def compare(x: Int, y: Int): Int = y - x
  }

  //in scala 3 instead of implicit we have using - only name was changed
  def extremes[A](list: List[A])(using ordering: Ordering[A]):(A,A)= {
    val sorted = list.sorted
    (sorted.head, sorted.last)
  }

  // implicit defs (syntesize new implicit values)
  case class Person(name: String)
  import scala.language.implicitConversions
  given string2PersonConversion: Conversion[String, Person] with {
    override def apply(x: String) = Person(x)
  }


}
