package advanced.base

object Monads {

  trait MonadTemplate[A]{
    def unit(value: A): MonadTemplate[A]
    def flatMap[B](f: A=> MonadTemplate[B]): MonadTemplate[B]
  }
  //monads are named differently in different languages such as: List, Option, Try, Future, Stream, Set

  //monad laws
  //left identity
  //unit(x).flatMap(f) == f(x)

  //right identity
  //aMonadInstance.flatMap(unit) == aMonadInstance

  //associativity
  //m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))
}
