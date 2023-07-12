package intro.learn.functional

object MapFilter extends App {

  val list = List(1, 2, 3, 4)
  val chars = List('a', 'b', 'c', 'd')

  private val combinations: List[String] = list.flatMap(i => chars.map(char => s"${i}${char}"))
  println(combinations)

  list.foreach(println)

  //for-comprehensions
  //compilator rewrites it to map/filter functions
  private val yieldCombinations = for{
    x <- list
    y <- chars
  } yield s"${x}${y}"

  println(combinations == yieldCombinations)

  // we can add guards
  private val withGuards = for {
    x <- list if x % 2== 0
    y <- chars
  } yield s"${x}${y}"

  println(withGuards)

  //foreach
  for {
    n <- list
  } println(n)

  //additional syntax
  list.map{ x => x * 2}

}
