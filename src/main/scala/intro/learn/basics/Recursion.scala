package intro.learn.basics

import scala.annotation.tailrec

object Recursion extends App {

  def factorial(n: Int): Int =
    if (n <= 1) 1
    else {
      println(s"Computing factorial of ${n} first I need factorial of ${n - 1}")
      val result = n * factorial(n - 1)
      println(s"Computed factorial of ${n}")
      result
    }

  //  println(factorial(5000))

  def tailRecustiveFactorial(n: Int): Int = {
    @tailrec // that annotation checks if function is tail recursive
    def helper(x: Int, accumulator: Int): Int =
      if (x <= 1) accumulator
      else helper(x - 1, x * accumulator)

    helper(n, 1)
  }

  println(tailRecustiveFactorial(50))

  //tail recursion
  // we don't need to store any intermediate results
  // recursion evaluation is the last command

  //when you need loops use tail recursion

  def tailRecConcatenation(s: String, n: Int): String = {
    @tailrec
    def helper(n: Int, acc: String): String =
      if (n <= 0) acc
      else helper(n - 1, acc + s)

    helper(n, "")
  }

  println(tailRecConcatenation("a", 4) == "aaaa")

  def tailRecIsPrime(n: Int): Boolean = {
    @tailrec
    def helper(current: Boolean, m: Int): Boolean = {
      if (!current) false
      else if (m == 1) true
      else helper(n % m != 0, m - 1)
    }

    helper(true, n / 2)
  }

  println(tailRecIsPrime(2) == true)
  println(tailRecIsPrime(3) == true)
  println(tailRecIsPrime(4) == false)
  println(tailRecIsPrime(5) == true)
  println(tailRecIsPrime(6) == false)

  def tailRecFibonacci(n: Int): Int = {
    def helper(m: Int, acc1: Int, acc2: Int): Int =
      if (m == n) acc1 + acc2
      else helper(m + 1, acc2, acc1 + acc2)

    if (n <= 2) 1
    else helper(3, 1, 1)
  }

  println(tailRecFibonacci(1) == 1)
  println(tailRecFibonacci(2) == 1)
  println(tailRecFibonacci(10) == 55)

  //rule of thumb - how many recursive calls you had in normal form - that many accumulators you need

}
