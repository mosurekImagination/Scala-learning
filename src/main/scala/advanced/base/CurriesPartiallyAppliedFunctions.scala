package advanced.base

object CurriesPartiallyAppliedFunctions extends App {

  //curried function
  val supperAdder: Int => Int => Int = x => y => x + y

  val add3 = supperAdder(3) // Int => Int = y => x + y

  println(add3(5))
  println(supperAdder(3)(5))

  //method
  def curriedAdder(x: Int)(y: Int) = x + y //curried method

  //we try to convert method into value of type function (higher order functions)
  // jvm limitations because methods are part of instances of classes
  //val add4 = curriedAdder(4) doesnt work without type
  val add4: Int => Int = curriedAdder(4)
  // lifting - transforming methods into functions | ETA-EXPANSION

  // functions != methods
  def inc(x: Int) = x + 1

  List(1, 2, 3).map(inc) // ETA-expansion done by compiler for us
  List(1, 2, 3).map(x => inc(x)) // ETA-expansion done by compiler for us

  //Partial function application
  val add5 = curriedAdder(5) _ //underscode => compiler, do ETA-expansion for me

  //excercise
  val simpleAddFunction = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int) = x + y

  def curriedAddMethod(x: Int)(y: Int) = x + y

  // add7
  val add7Function = y => simpleAddFunction(7, y)
  val add7Method = y => simpleAddMethod(7, y)
  val add7Curried = curriedAddMethod(7) _

  // variations
  val add7CurriedVariation = y => curriedAddMethod(7)(y)
  val add7CurriedLib = simpleAddMethod.curried(7)
  val add7CurriedWithType: Int => Int = curriedAddMethod(7)
  val add7CurriedHacky: Int => Int = simpleAddMethod(7, _: Int) // hacky

  //underscores
  def concatenator(a: String, b: String, c: String) = a + b + c

  val insertName = concatenator("Hello ", _: String, " nice to meet you")
  println(insertName("Tom"))

  //good for smaller, concrete functions

  val format = (number: Double, formatter: String) => formatter.format(number)
  val format4 = format(_: Int, "%4.2f")
  val format8 = format(_: Int, "%8.6f")
  println(List(1,2,3).map(format4))
  println(List(1,2,3).map(format8))

  //byName and byFunction
  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42 // these methods are different in a sense that they cannot be assigned to higher order functions
  def parenMethod(): Int = 42

  byName(23)
  byName(method)
  byName(parenMethod())
  //byName(parenMethod)
  //byName(()=>42) not ok
  byName((()=>42)()) //providing lambda and instantly calling it

  //byFunction(method) not ok as method is evaluated to its value - compiler doesn't do ETA-expansion
  byFunction(parenMethod) //ETA-expansion

}
