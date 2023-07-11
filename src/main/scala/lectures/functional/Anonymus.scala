package lectures.functional

object Anonymus extends App {

  val oldDoubler: Function1[Int, Int] = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 * 2
  }
  //syntactic sugar for creating new Function1 with override def apply
  //anonymous function == lambda

  val functionalDoubler: Int => Int = (x: Int) => x * 2
  // type recognition
  val functionalDoubler2: Int => Int = x => x * 2
  val functionalDoubler3 = (x: Int) => x * 2

  val func1: Int => Int = a => a
  //multiple params needs to be in brackets
  val func2: (Int, Int) => Int = (a, b) => a + b
  //no params
  val func3 = () => 5

  println(func3) // function
  println(func3()) //5

  //curly braces with lambdas
  val func4 = { a: Int =>
    a + 5
  }

  //MOAR syntactic sugar
  val moar1 = (a: Int) => a + 1
  val moar2: (Int) => Int = _ + 1

  val moar3: (Int, Int) => Int = (a, b) => a + b
  val moar4: (Int, Int) => Int = _ + _ //a + b

}
