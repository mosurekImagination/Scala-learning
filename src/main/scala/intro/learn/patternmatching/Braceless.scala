package intro.learn.patternmatching

object Braceless extends App {

  if (1 > 2) true else false

  if (1 > 2) {
    true
  } else {
    false
  }

  if (1 > 2) true
  else false

  //scala 3
  if 2 > 3 then
    true
  else
    false

  //scala 3 then sections act as code block
  if 2 > 3 then
    val a = 5
    println
    true
  else
    val b = 6
    println
    false

  // scala 3 oneliner
  if 2 > 3 then true else false

  //scala 3 for comprehensions
  for {
    n <- List(1, 2, 3)
    s <- List("black", "white")
  } yield s"$n$s"

  for
    n <- List(1, 2, 3)
    s <- List("black", "white")
  yield s"$n$s"

  //scala 3 pattern matching
  2 match
    case 1 => "1"
    case 2 => "2"


  // stay consisted

  //methods without braces
  def computeSomething(a: Int): Int =
    val a = 5
    println


    5

  println(computeSomething(5))

  //classes traits etc

  class Animal: // colon token to show compiler that we will use block
    def eat() =
      println("Eating")
    end eat

  end Animal // optional end token to let compiler
  // and other people know where your block is ending

  //we can use end tokens for everything else

}
