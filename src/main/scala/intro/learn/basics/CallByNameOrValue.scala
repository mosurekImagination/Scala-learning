package intro.learn.basics

import scala.annotation.tailrec

object CallByNameOrValue extends App {

  def calledByValue(x: Long): Unit = {
    println(s"by value 1: ${x}")
    println(s"by value 2: ${x}")
  }

  //expression is passed as it (literally) and evaluated each time
  //it delays evaluation of parameters
  def calledByName(x: => Long): Unit = {
    println(s"by name 1: ${x}")
    println(s"by name 2: ${x}")
  }

  def function = {
    System.nanoTime()
  }

  calledByValue(function)
  calledByName(function)

  def infinite():Int = {1 + infinite() }
  //a is evaluated immediately and assigned to a
  def notWorkingFunction(a: Int) = println("NotWorkingFunction")
  //a is not evaluated - no errors
  def delayed(a: => Int) = println("delayed")

  try{ notWorkingFunction(infinite())} catch {case _=> println("Throwed exception")}
  delayed(infinite())
}
