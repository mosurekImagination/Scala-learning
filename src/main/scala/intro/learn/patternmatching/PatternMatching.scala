package intro.learn.patternmatching

import scala.util.Random

object PatternMatching extends App {

  val random = new Random
  val x = random.nextInt(10)

  //Pattern Matching, looks like switch
  val string = x match {
    case 1 => "One"
    case 2 => "Two"
    case _ => "3-10" // _ WILDCARD
  }

  //1. Decompose values
  case class Person(name: String, age: Int)

  val tom = Person("Tom", 20)
  val greeting = tom match {
    case Person(name, age) if age < 21 => s"I'm ${name} and I'm ${age} years old" // guard
    case Person(name, age) => s"I'm ${name} and I'm ${age} years old"
    case _ => "Wildcard"
  }
  //cases are matched in order
  //if there is no match => MatchError - use wildcard
  //type of PM expression is unified type of all returns
  // Pattern Matching expression works great with case classes

  //2
  sealed class Animal

  case class Dog(breed: String) extends Animal

  case class Parrot(greeting: String) extends Animal

  val animal: Animal = Dog("some breed")

  animal match {
    case Dog(someBreed) => s"some dog with breed ${someBreed}"
  }

  trait Expr

  case class Number(n: Int) extends Expr

  case class Sum(e1: Expr, e2: Expr) extends Expr

  case class Prod(e1: Expr, e2: Expr) extends Expr

  def printExpression(e: Expr): String = {
    def parenthesis(e1: Expr) = {
      e1 match {
        case Sum(_, _) => s"(${printExpression(e1)})"
        case _ => printExpression(e1)
      }
    }

    e match {
      case Number(n) => n.toString
      case Sum(e1, e2) => printExpression(e1) + " + " + printExpression(e2)
      case Prod(e1, e2) => parenthesis(e1) + " * " + parenthesis(e2)
    }
  }

  println(printExpression(
    Sum(Sum(Number(2), Number(3)), Number(4))
  ))

  println(printExpression(
    Prod(Sum(Number(2), Number(3)), Number(4))
  ))
  println(printExpression(
    Sum(Prod(Number(2), Number(3)), Number(4))
  ))


}
