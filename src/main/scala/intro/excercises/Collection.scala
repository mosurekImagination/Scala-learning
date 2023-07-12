package intro.excercises

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

  def forEach(lambda: A => Unit): Unit

  def sort(lambda: (A, A) => Int): MyList[A]

  def zipWith[B, C](otherList: MyList[B], zip: (A, B) => C): MyList[C]

  def fold[B](start: B)(lambda: (B,A) => B): B

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

  override def forEach(lambda: Nothing => Unit): Unit = ()

  override def sort(lambda: (Nothing, Nothing) => Int): MyList[Nothing] = Empty

  override def zipWith[B, C](otherList: MyList[B], zip: (Nothing, B) => C): MyList[C] = Empty

  override def fold[B](start: B)(lambda: (B, Nothing) => B): B = start
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

  override def forEach(lambda: A => Unit): Unit = {
    lambda(h)
    tail().forEach(lambda)
  }

  override def sort(compare: (A, A) => Int): MyList[A] = {
    def insert(x: A, sortedList: MyList[A]): MyList[A] =
      if (sortedList.isEmpty()) new ConsList(x, Empty)
      else if (compare(x, sortedList.head()) <= 0) new ConsList(x, sortedList)
      else ConsList(sortedList.head(), insert(x, sortedList.tail()))

    val sortedTail = t.sort(compare)
    insert(h, sortedTail)
  }

  override def zipWith[B, C](otherList: MyList[B], zip: (A, B) => C): MyList[C] = new ConsList[C](zip(h, otherList.head()), tail().zipWith(otherList.tail(), zip))

  override def fold[B](start: B)(lambda: (B, A) => B): B = tail().fold(lambda(start, head()))(lambda)
}

object app extends App {
  val list = new ConsList(1, new ConsList(2, new ConsList(3, Empty)))
  val list2 = new ConsList(1, new ConsList(2, new ConsList(3, Empty)))

  // nothing
  println(new ConsList(1, Empty).tail() == Empty)
  println(Empty.map("D") == Empty)

  //list
  println(list.head() == 1)
  println(list.tail().toString() == "23")
  println(list.add(4).head().toString() == "4")
  println((list ++ new ConsList(4, new ConsList(5, Empty))).toString() == "12345")
  println(list == list2) //case class equals


  //higher order functions
  println(list.map(_ * 2).toString() == "246")
  println(list.filter(_ % 2 == 0).toString() == "2")
  println(list.flatMap(elem => new ConsList(elem, new ConsList(elem + 1, Empty))).toString() == "122334")

  //HOFs
  println("HOFS")
  list.forEach(print(_))
  println(list.zipWith[Int, Int](list2, _ + _).toString() == "246")
  println(list.fold(1)((a: Int, b: Int) => a * b) == 6)
  println(list.sort((a, b) => b - a).toString() == "321")


  //it works for our custom collection as we have implemented map,filter, flatmap functions
  val values = for {
    x <- list
  } yield (x * 2)
  println(values)

}