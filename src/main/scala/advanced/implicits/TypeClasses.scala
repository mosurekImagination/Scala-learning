package advanced.implicits

object TypeClasses extends App {

  //TYPE CLASS
  trait MyTypeClassTemplate[T]{
    def action(value:T): String
  }

  object MyTypeClassTemplate{
    def apply[T](implicit instance: MyTypeClassTemplate[T]) = instance
  }

  trait Equal[T]{
    def apply(a: T, b:T): Boolean
  }

  object DoubleEquality extends Equal[Double]{
    def equal(a: Double, b: Double)(implicit equalizer: Equal[Double]): Boolean = equalizer.apply(a,b)
    override def apply(a: Double, b: Double): Boolean = a == b
  }

  implicit object DoubleIntEquality extends Equal[Double]{
    override def apply(a: Double, b: Double): Boolean = a.toInt == b.toInt
  }

  println(DoubleEquality.equal(1.1, 1.3))

  //AD-HOC polymorphism


  /*
  *  - type classes
     - type class instances (some of which are implicit)
     - conversion with implicit classes
  * */

  // we can extract implicit value and assign it with implicitly method
  val extractedValue = implicitly[Equal[Double]]
  println(extractedValue)
}
