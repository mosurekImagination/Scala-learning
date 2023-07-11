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
}
