package advanced.implicits

object Implicits extends App {

  val a = 1 -> 5 // -> it is implicit method

  case class Person(name: String) {
    def greet = s"Hi, my name is $name"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)

  println("Peter".greet) // after simple checks compilator checks if something implicit fits greet method

  //scala.Prefer

  //Implicits ( used as implicit parameters)
  // val/var
  // object
  // accessor methods = defs with no parentheses

  implicit val ordering: Ordering[Int] = Ordering.fromLessThan((a,b) => a.compareTo(b) < 0)

  //implicit scope
  // - normal scope = LOCAL SCOPE
  // - imported scope
  // companions of all types involved in the method signature

  // if implicit is mostly used and it is possible define implicits in companion objects
  // in other case put it in separate object and let user import it explicitly
}
