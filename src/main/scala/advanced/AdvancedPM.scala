package advanced

object AdvancedPM extends App {

  val numbers = List(1)


  //if we cannot use case classes, we can implement unapply method

  class Person(val name: String, val age:Int)

  object Person:
    def unapply(arg: Person): Option[(String, Int)] = Some(arg.name, arg.age)

  val someone = Person("aga", 15)

  someone match
    case Person(n,a) => n + a

  //compilator looks for method unapply which will return tuple with 2 things
  //and we just implemented it
  //then it tries to execute it and see if option is empty
  //if it is not then matching is successful

  //custom patterns
  object PersonPattern:
    def unapply(arg: Person): Option[(String, Int)] = Some(arg.name, arg.age)
    def unapply(arg: Int): Option[String] = Some("My Greeting")

  val a = someone.age match
    case PersonPattern(greeting) => greeting

  println(a)

  val n:Int = 44

  object even:
    //def unapply(arg: Int): Option[Int] = if arg % 2 == 0 then Some(arg) else None
    def unapply(arg: Int): Boolean = arg % 2 == 0


  val result = n match
    case even() => s"Even number"
    case _ => "Other"

  println(result)

  //infix patterns
  case class Or[A,B](a:A, b:B)
  val either = Or(2,"two")
  either match
    case number Or string => s" $number or $string"
//    case Or(number,string) => s" $number or $string"


  //decomposing sequences
  val arargs = numbers match
    case List(1, _*) => "starting with 1"


  abstract class MyList[+A] {
    def head: A = ???

    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]

  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myList match {
    case MyList(1, 2, _*) => "starting with 1, 2"
    case _ => "something else"
  }
  // it looks for unapplySequence method

  println(decomposed)

  // custom return types for unapply
  // isEmpty: Boolean, get: something.

  //instead of Optional
  abstract class Wrapper[T] {
    def isEmpty: Boolean

    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      def isEmpty = false

      def get = person.name
    }
  }

  println(someone match {
    case PersonWrapper(n) => s"This person's name is $n"
    case _ => "An alien"
  })

}
