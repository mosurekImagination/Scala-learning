package advanced.base

import scala.util.Try

object Sugar extends App {

  //1. single param can be provided as code block
  def singleParam(a: Int) = a

  singleParam {
    val a = 5
    5
  }
  Try {

  }
  List(1, 2, 3).map { x => x + 1 }

  //2. Single abstract method (like in java)
  trait Action {
    def act(x:Int): Int
  }

  val instance: Action = new Action:
    override def act(x: Int): Int = 5

  //we can reduce instantiation to lambda

  val instance2: Action = (x: Int) => 5

  abstract class SomeClass{
    def implementedMethod()= 5
    def abstractMethod(a: Int): String
  }
  val instance3: SomeClass = (a:Int) => "Something"

  // :: and #:: methods
  //scala spec: last char decides associativity of method :(:) -> right association
  2 :: List(3,4)
  // List(3,4).::(2) !!
  val equivalent = List(3,4).::(2)
  class MyStream[T]{
    def -->:(value:T): MyStream[T] = this
  }
  val correct = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  //4. multi-word method naming
  def `backstick method names`() = 5

  //5. infix types
  class Composite[A,B]
  val composite: Composite[Int,String] = ???
  val composite2: Int Composite String = ???

  //6. update method. Special like apply
  val array = Array(1,2,3)
  array(2) = 3 // rewritten to anArray.update(2,3)
  //used in mutable collections

  //7. setters for mutable containers
  class Mutable:
    private var internalMember:Int = 0
    def member = internalMember
    def member_=(value:Int) = internalMember = value

  val mutable = new Mutable
  mutable.member = 5 //reqritten as mutable.member_=(5)

}
