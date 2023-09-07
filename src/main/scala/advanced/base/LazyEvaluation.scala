package advanced.base

object LazyEvaluation extends App {

  lazy val x:Int = throw RuntimeException() //with lazy evaluation we evaluate expression once, only when it is needed

  //examples of implications
  def sideEfefctsConditions: Boolean = {
    println("Boo")
    true
  }
  def simpleCondition = false

  lazy val lazyCondition = sideEfefctsConditions

  println(if (simpleCondition && lazyCondition) "yes" else "no") // as first condition in AND is already false we don't need to evaluate lazy condition

  // in conjuction with call by name
  def byNameMethod(n: => Int) = n + n + n
  def retrieveHardValue = {
    Thread.sleep(1000)
    42
  }
  byNameMethod(retrieveHardValue) //here we are waiting 3 seconds! each byName has its own execution!

  //it is better to use lazy vals!
  def betterByNameMethod(n: => Int) = {
    //call by need
    lazy val t = n
    n + n + n
  }

  //lazy filtering!
  val list = List(1,2,4,5,6).withFilter(x => {
    println(s"filtering ${x}")
    x < 5
  })
  //if we don't use list - filtering is even not evaluated!

  //for-comprehensions use withFilter with guards!
  for {
    a <- List(1,2,3) if a % 2 == 0//using lazy vals!
  } yield a + 1
  //is the same as:
  List(1,2,3).withFilter(_%2 == 0).map(_ + 1 )


}
