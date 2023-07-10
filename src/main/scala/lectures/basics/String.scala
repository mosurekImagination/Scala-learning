package lectures.basics

import scala.annotation.tailrec

object String extends App {

  val str = "Hello"
  val numberString = "45"
  val number = numberString.toInt
  println('a' +: str :+ 'z')

  //s interpolators
  println(s"Hello $numberString - $number - ${1+2}")

  //f interpolators -- similar to printf -- can check for type correctness
  val speed = 1.2f
  println(f"$str%s speed is $speed%2.4f burgers per minute")

  //raw interpolator
  println(raw"This is a \n newline") //n is shown
  val inject = "This is a \n newline"
  println(raw"$inject") //n newline is shown
}
