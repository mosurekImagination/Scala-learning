package advanced.implicits

object ExtensionMethods extends App {

  case class Person(name:String){
    def greet() = s"Hi, I'm ${name}"
  }

  extension (string:String) {
    def greetAsPerson(): String = Person(string).greet()
  }

  val greeting = "Tom".greetAsPerson()

  // extension methods <=> implicit classes
  // we don't need boilerplate to extend classes with additonal methods

  extension (value: Int) {
    def isEven() = value % 2 ==0
  }

  println(2.isEven())

  //generic extensions
  extension [A](list: List[A]){
    def ends: (A,A) = (list.head, list.last)
  }

  List(1,2,3).ends
}
