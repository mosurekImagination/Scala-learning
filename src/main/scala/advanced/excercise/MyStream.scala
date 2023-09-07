package advanced.excercise

import scala.annotation.tailrec

abstract class MyStream[+A] {
  def isEmpty: Boolean

  def head: A

  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] //prepend operator

  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B]

  def map[B >: A](f: A => B): MyStream[B]

  def foreach(f: A => Unit): Unit

  def flatMap[B >: A](f: A => MyStream[B]): MyStream[B]

  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A]

  def takeAsList(n: Int): List[A] = take(n).toList()

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] = {
    if (isEmpty) acc.reverse
    else tail.toList(head :: acc)
  }

}

object EmptyStream extends MyStream[Nothing] {
  override def isEmpty: Boolean = true

  override def head: Nothing = throw new NoSuchElementException

  override def tail: MyStream[Nothing] = EmptyStream

  override def #::[B >: Nothing](element: B): MyStream[B] = MyInfiniteStream[B](element, this)

  override def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  override def map[B >: Nothing](f: Nothing => B): MyStream[B] = EmptyStream

  override def foreach(f: Nothing => Unit): Unit = ()

  override def flatMap[B >: Nothing](f: Nothing => MyStream[B]): MyStream[B] = EmptyStream

  override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = EmptyStream

  override def take(n: Int): MyStream[Nothing] = EmptyStream
}

class MyInfiniteStream[+A](elem: A, stream: => MyStream[A]) extends MyStream[A] {
  override def isEmpty: Boolean = false

  override val head: A = elem

  override lazy val tail: MyStream[A] = stream // lazy evaluation + call by Name = call by need

  override def #::[B >: A](element: B): MyStream[B] = MyInfiniteStream(element, this)

  override def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = MyInfiniteStream[B](head, tail ++ anotherStream)

  override def map[B >: A](f: A => B): MyStream[B] = new MyInfiniteStream[B](f(head), tail.map(f))

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override def flatMap[B >: A](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

  override def filter(predicate: A => Boolean): MyStream[A] =
    if (predicate(head)) MyInfiniteStream(head, tail.filter(predicate))
    else tail.filter(predicate)

  override def take(n: Int): MyStream[A] =
    if (n <= 0) EmptyStream
    else if (n == 1) new MyInfiniteStream(head, EmptyStream)
    else new MyInfiniteStream(head, tail.take(n - 1))

}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] = MyInfiniteStream[A](start, MyStream.from(generator(start))(generator))
}

object Playground2 extends App {
  val naturals = MyStream.from(1)(_ + 1)
  println(naturals.tail.tail.head)
  println((0 #:: naturals).head)

  naturals.take(100) foreach println

  println(naturals.flatMap(x=> MyInfiniteStream(x, MyInfiniteStream(x+1, EmptyStream))).take(10).toList())
  println("filtering")
  naturals.filter(_ < 10).take(9).toList() foreach( println)

  //fibonacci stream
  def fibonacciStream(first: Int, second: Int): MyStream[Int] = MyInfiniteStream[Int](first, fibonacciStream(second, first + second))
  val stream = fibonacciStream(1,1)
  println(stream.take(6).toList())
}