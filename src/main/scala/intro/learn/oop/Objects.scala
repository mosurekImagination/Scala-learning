package intro.learn.oop

object Objects extends App {
  //SCALA DOES NOT HAVE CLASS_LEVEL FUNCTIONALITY "static"
  //Scala object = Singleton instance
  //that object is an instance - the same for all created class instances
  //object Person is companion to class Person
  object Person {
  def from(mother:Person, father: Person) = new Person() // factory method
  def apply(mother:Person, father: Person) = new Person() // factory method with apply
  }

  //object Person is companion to class Person
  class Person {

  }
  Person.from(new Person(), new Person())
  Person(new Person(), new Person()) // looks like constructor but this is apply method in Person companion object
}
