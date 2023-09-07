package advanced.excercise

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {

  override def apply(v1: A): Boolean = contains(v1)

  def contains(elem: A): Boolean

  def +(elem: A): MySet[A]

  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(f: A => Boolean): MySet[A]

  def forEach(f: A => Unit): Unit

  def -(elem: A): MySet[A]

  def intersection(set: MySet[A]): MySet[A]

  def difference(set: MySet[A]): MySet[A]

  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {

  override def contains(elem: A): Boolean = false

  override def +(elem: A): MySet[A] = NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = EmptySet[B]()

  override def flatMap[B](f: A => MySet[B]): MySet[B] = EmptySet[B]()

  override def filter(f: A => Boolean): MySet[A] = this

  override def forEach(f: A => Unit): Unit = ()

  override def -(elem: A): MySet[A] = this

  override def intersection(set: MySet[A]): MySet[A] = this

  override def difference(set: MySet[A]): MySet[A] = this

  override def unary_! : MySet[A] = PropertyBasedSet[A](_=>true)
}

//all elements of type A which satisfy a property
// { x in A | property(x) }
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A]{
  override def contains(elem: A): Boolean = property(elem)
  // { x in A | property(x) } + element = { x in A | property(x) || x == element }
  override def +(elem: A): MySet[A] = PropertyBasedSet(x=> property(x) || x == elem)

  override def ++(anotherSet: MySet[A]): MySet[A] = PropertyBasedSet(x=> property(x) || anotherSet(x))
  override def map[B](f: A => B): MySet[B] = politelyFail

  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

  override def filter(f: A => Boolean): MySet[A] = PropertyBasedSet(x => property(x) && f(x))

  override def forEach(f: A => Unit): Unit = politelyFail

  override def -(elem: A): MySet[A] = filter(x => x != elem)

  override def intersection(set: MySet[A]): MySet[A] = filter(set)

  override def difference(set: MySet[A]): MySet[A] = filter(!set)

  override def unary_! : MySet[A] = new PropertyBasedSet(x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole")
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean = elem == head || tail.contains(elem)

  override def +(elem: A): MySet[A] = if (this.contains(elem)) this else NonEmptySet(elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = tail ++ anotherSet + head

  override def map[B](f: A => B): MySet[B] = tail.map(f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = f(head) ++ tail.flatMap(f)

  override def filter(f: A => Boolean): MySet[A] = if (f(head)) NonEmptySet(head, tail.filter(f)) else tail.filter(f)

  override def forEach(f: A => Unit): Unit =
    f(head)
    tail.forEach(f)

  override def -(elem: A): MySet[A] = if(head == elem) tail else tail-elem + head

  override def intersection(set: MySet[A]): MySet[A] = filter(set) // intersection = filtering!

  override def difference(set: MySet[A]): MySet[A] = filter(!set)

  override def unary_! : MySet[A] = PropertyBasedSet[A](x => !this.contains(x))
}

object MySet{
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)

    buildSet(values,  EmptySet[A])
  }
}

object Playground extends App {
  val s = MySet(1,2,3,4)
  s + 5 ++ MySet(0, 1) forEach print
  s.map(_ * 2) flatMap (it => MySet(it*2, it*3)) forEach println

  val negativeS = !s //all naturals but 1,2,3,4
  println(negativeS(1))
  println(negativeS(5))

  private val without5: MySet[Int] = negativeS filter (x => x != 5)
  println(without5 contains 5)
  println(without5 + 5 contains 5)

}