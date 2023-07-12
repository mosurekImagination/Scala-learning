package intro.learn.functional

object Sequences extends App {

  //sequence
  val seq = Seq(1,2,3,4)
  println(seq)
  println(seq.reverse)

  //range
  val range = 1 to 10
  val until = 1 until 10

  //list
  val list = List(1,2)
  //prepending
  1::list
  1 +: list
  list :+ 1
  val fill = List.fill(5)("apples")
  fill.mkString("-")

  //arrays
  //interoperable with javas arrays
  val numbers = Array(1,2,4)
  Array.ofDim[Int](4)
  //arrays can be mutated
  numbers(2) = 0 //syntax sugar for numbers.update(2,0)

  val arraySeq: Seq[Int] = numbers //implicit conversion to WrappedArray
  println(arraySeq)

  //vector
  //very good performance log32 based - time goes up slowly
  //is based on 32 elements tree structure
  //during update needs to update the whole 32 elements chunk
  //depth of tree is small
  val vector = Vector(1,2,3)




}
