package lectures.oop

object CaseClasses extends App{
  case class Person(name: String, age: Int)
  // like data classes in Kotlin
  // 1.class parameters are fields
  // 2. fields in toString()
  // 3. equals and hashcode implemented based on fields
  // 4. copy methods
  // 5. have companion objects
  val thePerson = Person("name", 15) // delegate to apply method
  // 6. they are serializable => can be used to send classes through the network
      // useful for scala actors
  // 7. they have extractor pattern - can be used in Pattern matching

  //there might be case object
  case object someObject{
    def name() = "SomeName"
  }

}
