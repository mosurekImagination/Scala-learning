package lectures.oop

import scala.annotation.tailrec

//class Person(name:String, age:Int) //class parameters are not fields
class Person(name: String, val age: Int =10) {
  println("I'm being evaluated during class construction")

  def this(name: String) = this(name, 0) // second constructor, pretty useless as it can contain only constructors evaluation

  def greet(name: String) = {
    println(s"${this.name} is saying hello to $name") //name doesnt need to be a field to be used in methods
  }

  def greet() = {
    println(s"$name is saying hello") // this is implicit
  }
}

object OOPBasics extends App {

  val person = Person("tom", 15)
  val person2 = Person("tom", 2)
  println(person.age)
}
