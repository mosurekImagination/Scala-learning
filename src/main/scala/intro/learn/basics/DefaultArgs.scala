package intro.learn.basics

import scala.annotation.tailrec

object DefaultArgs extends App {

  def trFact(n: Int, acc: Int = 1): Int = //default parameter
    if (n <= 1) acc
    else trFact(n - 1, n * acc)

  val fact10 = trFact(10, 1)
  val fact10Default = trFact(10)
  val fact10DNamed = trFact(acc = 1, n = 10) //named parameters, even in different order

  println(fact10 == fact10Default)
  println(fact10 == fact10DNamed)
}
