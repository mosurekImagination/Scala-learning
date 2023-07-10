package excercises

import java.util

abstract class MyList {
  def head(): Int

  def tail(): MyList

  def isEmpty(): Boolean

  def add(a: Int): MyList

  override def toString(): String = head() + tail().toString()
}

object Empty extends MyList {
  override def head(): Int = throw new NoSuchElementException

  override def tail(): MyList = throw new NoSuchElementException

  override def isEmpty(): Boolean = true

  override def add(a: Int): MyList = new ConsList(a, Empty)

  override def toString(): String = ""
}

class ConsList(h: Int, t: MyList) extends MyList {
  override def head(): Int = this.h

  override def tail(): MyList = t

  override def isEmpty(): Boolean = false

  override def add(a: Int): MyList = ConsList(a, this)

}


object app extends App {
  val list = new ConsList(1, new ConsList(2, new ConsList(3, Empty)))
  println(list.head())
  println(list.tail())
  println(list.add(4).head())
}