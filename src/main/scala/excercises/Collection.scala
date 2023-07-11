package excercises

abstract class MyList[+A] {
  def head(): A

  def tail(): MyList[A]

  def isEmpty(): Boolean

  def add[B >: A](a: B): MyList[B]

  override def toString(): String = head().toString + tail().toString()

  def map[B](transformer: A => B): MyList[B]

  def filter(filter: A => Boolean): MyList[A]
  def flatMap[B](transformer: A => MyList[B]): MyList[B]

  def ++[B >: A](list: MyList[B]): MyList[B]
}

case object Empty extends MyList[Nothing] {
  override def head(): Nothing = throw new NoSuchElementException

  override def tail(): Nothing = throw new NoSuchElementException

  override def isEmpty(): Boolean = true

  override def add[B >: Nothing](a: B): MyList[B] = new ConsList(a, Empty)

  override def toString(): String = ""

  override def map[B](transformer: Nothing => B): MyList[B] = Empty

  override def filter(filter: Nothing => Boolean): MyList[Nothing] = Empty
  override def flatMap[B](transformer: Nothing => MyList[B]): MyList[Nothing] = Empty

  override def ++[B >: Nothing](list: MyList[B]): MyList[B] = list
}

case class ConsList[+A](h: A, t: MyList[A]) extends MyList[A] {
  override def head(): A = this.h

  override def tail(): MyList[A] = t

  override def isEmpty(): Boolean = false

  override def add[B >: A](a: B): MyList[B] = new ConsList(a, this)

  override def map[B](transformer: A => B): MyList[B] = new ConsList(transformer(h), t.map(transformer))

  override def filter(predicate: A => Boolean): MyList[A] = if (predicate(h)) new ConsList(h, t.filter(predicate)) else t.filter(predicate)
  override def ++[B >: A](list: MyList[B]): MyList[B] = new ConsList(h, t ++ list)

  override def flatMap[B](transformer: A => MyList[B]): MyList[B] = transformer(head()) ++ tail().flatMap(transformer)
}

object app extends App {
  val list = new ConsList(1, new ConsList(2, new ConsList(3, Empty)))
  val list2 = new ConsList(1, new ConsList(2, new ConsList(3, Empty)))

  // nothing
  println(new ConsList(1, Empty).tail() == Empty)
  println(Empty.map( "D") == Empty)

  //list
  println(list.head() == 1)
  println(list.tail().toString() == "23")
  println(list.add(4).head().toString() == "4")
  println((list ++ new ConsList(4, new ConsList(5, Empty))).toString() == "12345")
  println(list == list2) //case class equals


  //higher order functions
  println(list.map(_ * 2).toString() == "246")
  println(list.filter(_ % 2 == 0).toString() == "2")
  println(list.flatMap(elem => new ConsList(elem, new ConsList(elem+1, Empty))).toString() == "122334")

}