package intro.learn.basics

object Functions extends App {

  def aFunction(a: String, b: Int) = "String " //after equal mark there is a single expression - code block { } is also a single expression!

  def withoutParameters() = "Hello"

  println(withoutParameters())

  def recursive(s: String, n: Int): String =
    if (n == 1) s
    else s + recursive(s, n - 1)

  println(recursive("Hello ", 5))

  //WHEN YOU NEED LOOPS, USE RECURSION

  def factorial(n: Long): Long =
    if (n == 1) n
    else n * factorial(n - 1)

  println(factorial(4) == 24)

  def fibonacci(n: Long): Long =
    if (n == 1 || n == 2) 1
    else fibonacci(n - 1) + fibonacci(n - 2)

  println(fibonacci(10) == 55)

  def isPrime(n: Long): Boolean = {
    def checkNumber (m: Long): Boolean = if (m == 1) true else if (n % m == 0) false else checkNumber(m - 1)

    checkNumber(n - 1)
  }

  println(isPrime(2L) == true)
  println(isPrime(3L) == true)
  println(isPrime(4L) == false)
  println(isPrime(5L) == true)
}
