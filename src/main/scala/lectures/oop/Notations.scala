package lectures.oop

object Notations extends App {
  class Person(val name: String, val movie: String) {
    def likes(movie: String) = this.movie == movie
    def +(person: Person) = s"${this.name } + ${person.name}"
    def unary_! = s"${this.name } im minus"
    def alive = true
    def apply():String = s"${name} and ${movie}"
  }

  val person = new Person("asdf", "movie")
  person.likes("movie")
  person likes "movie" //infix notation / operation notation / syntactic sugar / more like natural language
  //works only when we have one parameter

  //all operators are methods!
  person + person
  // 1 + 2

  //prefix notation
  val x = -1
  val y = 1.unary_-
  !person
  //unary operation works only with - + ! ~

  //postfix notation
  //breaks barriers between object oriented programming and functional programming
  person.apply(); //apply evaluation
  person() //apply evaluation
}
