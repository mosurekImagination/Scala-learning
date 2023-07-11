package lectures.functional

object Curry extends App {

  def nTimes(f: (Int) => Int, n: Int, x: Int): Int =
    if (n <= 0) x
    else nTimes(f, n - 1, f(x))

  def nTimesLambda(f: (Int) => Int, n: Int): Int => Int = x =>
    if (n <= 0) x
    else nTimesLambda(f, n - 1)(f(x))

  val adder4 = nTimesLambda(x => x + 1, 4)
  println(nTimes(x => x + 1, 4, 1) == 5)
  println(adder4(1) == 5)

  //currying = functions with multiple parameter lists
  //parametersList
  def curriedFormatter(c: String)(x: Double) = c.format(x)

  def standardFormatter: (Double) => String = curriedFormatter("%2.2f")

  def preciseFormatter: (Double) => String = curriedFormatter("%2.10f")

  println(standardFormatter(Math.PI))
  println(preciseFormatter(Math.PI))

  def toCurry(f: (Int, Int) => Int): Int => Int => Int =
    (x: Int) => (y: Int) => f(x, y)

  def fromCurry(f: Int => Int => Int): (Int, Int) => Int = (x: Int, y: Int) => f(x)(y)

  def compose[A, B, C](f: (B => C), g: A => B)(x: A): C = f(g(x))

  def andThen[A, B, C](f: (A => B), g: B => C)(x: A): C = g(f(x))

  val add1AndMultiply2 = (number: Int) => compose((x: Int) => x + 1, (x: Int) => x * 2)(number)
  val add1AndThenMultiply2 = (number: Int) => andThen((x: Int) => x + 1, (x: Int) => x * 2)(number)
  println(add1AndMultiply2(1)==3)
  println(add1AndThenMultiply2(1) == 4)
}
