package intro.learn.oop

object Generics extends App {

  class MyList[A]{
    //B is supertype of A
    //when we add a Dog, then list is transformed to list of Animals
    //A - CAT, B - DOG
    def add[B >: A](element:B):MyList[B] = ???
  }

  object MyList{
    def empty[A]: MyList[A] = ???
  }

  //variance problem
  class Animal
  class Dog extends Animal
  class Cat extends Animal

  //does list of Cat extend list of Animal?

  //1. yes - COVARIANCE
  class CovariantList[+A]
  val animal: Animal = new Cat
  val animalList: CovariantList[Animal] = new CovariantList[Cat]
  //animalList.add(new Dog) ??? Hard question

  //2. no - invariant
  class InvariantList[A]
  val invariantAnimalList: InvariantList[Animal] = new InvariantList[Animal] //new InvariantList[Cat] doesnt work

  //3. Hell, no! Contravariance
  class ContravariantList[-A]
  val contravariantList: ContravariantList[Cat] = new ContravariantList[Animal]

  //Bounded types
  class Cage[A <: Animal](animal: A)
  class Car
//  new Cage(new Car())
}
