package intro.learn.oop

object Inheritance extends App {

  class Animal {
    def eat = println("eating")
    final def live() = println("this method cannot be overridden")
  }
  class Cat extends Animal
  class Dog extends Animal{
    override def eat: Unit = println("Dog eating")
  }
  new Cat().eat
  new Dog().eat

  //preventing overrides
  //final on member (function ex)
  //final on the entire class
  //seal the slass - extend classes in THIS FILE, prevent extensions in other clases

  abstract class Human{
    def eat: Unit
    val birth: String
  }
  class HomoSapiens extends Human{
    override val birth: String = "1990"
    def eat: Unit = println("eating homosapiens")
  }

  class HomoRunner extends Human with Runner{
    override val birth: String = "1990"

    def eat: Unit = println("eating homosapiens")
    override def speed(): Int = 10
  }
  trait Runner{
    def speed(): Int
  }
  //traits can be extended along with the classes

  //traits vs abstract classes
  //you can extend multiple traits. Scala has single class inheritance
  //traits = behaviours; class = type of "things"
  // Everything is subtype of Any
  // AnyVal is for primitives
  // AnyRef for objects
  // AnyRef is superclass for null - so we can assign null wherever we have anyref
  // Nothing is inherited from AnyVal and Null, so we can use nothing in AnyRef + Any Val
}
