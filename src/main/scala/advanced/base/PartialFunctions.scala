package advanced.base

object PartialFunctions extends App {

  val aFunction = (x: Int) => x + 1
  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else throw new FunctionNotApplicableException
    class FunctionNotApplicableException extends RuntimeException

  val nicerFussyFunction = (x: Int) => x match
    case 1 => 42
    case 2 => 56
  // proper fanction, can be assigned to Function type
  // {1,2} => Int
  // as that function accept only subset of Int it is called partial function


  val partialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
  } // partial function value
  // partial function, can be assigned to partial function type
  // equivalent of nicerFussyFunction
  // based on pattern matching
  println(partialFunction(2))
  // println(partialFunction(12)) throws MatchError

  // partial function utilities
  println(partialFunction.isDefinedAt(1))
  println(partialFunction.isDefinedAt(3))

  // lifting partial function to a function
  val lifted = partialFunction.lift // PF -> Int => Option[Int]
  println(lifted(1)) // Some
  println(lifted(5)) // None

  val chainedPartialFunction = partialFunction.orElse[Int, Int] {
    case 45 => 99
  }
  println(chainedPartialFunction(45) == 99)

  // PF is a subtype of Total function
  val aMappedList = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }
  println(aMappedList)

  // PF can only have ONE parameter type

  val manualFunction = new PartialFunction[Int, Int] {
    override def apply(v1: Int): Int = v1 match
      case 1 => 2
      case 2 => 3

    override def isDefinedAt(x: Int): Boolean = x == 1 || x == 2
  }
  
}

