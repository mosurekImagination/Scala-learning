package lectures.oop

object Anonymous extends App {
   abstract class Animal{
     def eat: Unit
   }
   val funnyAnimal:Animal = new Animal {
     override def eat: Unit = print("haha")
   }
}
