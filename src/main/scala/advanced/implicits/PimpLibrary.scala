package advanced.implicits

object PimpLibrary extends App {

  //type enrichtment = pimping
  implicit class RichInt(value:Int){
    def isEven: Boolean = value % 2 == 0
    def sqrt: Double = Math.sqrt(value)
  }

  println(4.sqrt==2) //compiler sees issue but search for all implicit classes it can build which have sqrt metchod
  println(6.isEven == true)

  //compiler doesn't do multiple implcit searches - in case multiple objects are available it will use only one

  implicit def stringToInt(string:String): Int = Integer.valueOf(string)
  println( "6" / 2)

}
