package lectures.functional

object Function extends App {

  //use functions as first class elements
  //problem: OOP
  //JVM was designed for OOP. It needs to use some tricks to use fp

  //Scala supports Function types
  //Function1, Function2, ... Function22

  val stringToIntConverter = new Function[String, Int] {
    override def apply(v1: String): Int = v1.toInt
  }
  //syntactic sugar (Int,Int => Int) is Function2[Int,Int,Int]
  val adder: ((Int, Int) => Int) = new Function2[Int, Int, Int] {
    override def apply(v1: Int, v2: Int): Int = v1 + v2
  }

  val function: (Int => Int => Int) = _ => _ => 5
  val typedFunction: Function1[Int, Function1[Int, Int]] = new Function1[Int, Function1[Int,Int]] {
  override def apply(v1: Int): Function1[Int, Int] = new Function1[Int,Int] {
    override def apply(v2: Int): Int = v1 + v2
  }
  }
  val typedFunctionAnonymous: (Int=>Int=>Int) = (a: Int) => (b:Int) => a + b
  //ALL SCALA FUNCTIONS ARE OBJECTS. INSTANCES OF FUNCTION DERIVED FROM FUNCTION_N CLASSES
  println(stringToIntConverter("3")) // automatic apply resolving
  val adder3 = typedFunction(3)
  val result = adder3(4)
  println(result == 7)
  println(typedFunction(3)(4) == 7) //curried function

  //high order functions like map etc. receives functions as parameter or returns functions
}
