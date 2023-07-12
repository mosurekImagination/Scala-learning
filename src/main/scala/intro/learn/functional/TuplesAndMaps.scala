package intro.learn.functional

object TuplesAndMaps extends App {
  val tuple = Tuple2[Int,String](2, "String")
  val sugar = (2,"String")
  sugar._1
  sugar._2
  sugar.copy()
  sugar.swap

  val map = Map()
  val map2 = Map(
    ("a", 5),
      ("b", 6)
  )
  val mapSugar = Map(
  "a"->5,
  "b"->6
  ).withDefaultValue(7)

  val pair = "c" -> 7
  mapSugar + pair
  //mapSugar.view.filter()

}
