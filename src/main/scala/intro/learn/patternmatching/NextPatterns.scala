package intro.learn.patternmatching

import intro.excercises.{ConsList, Empty, MyList}

object NextPatterns extends App {

  //1 - constants
  val x:Any = "Scala"

  x match {
    case 1 => "number"
    case "Scala" => "String"
    case true => "Boolean"
    case NextPatterns => "Object"
  }

  //2 - match anything

  // wildcard
  x match {
    case _ => "Everything"
  }

  //variable
  x match {
    case something => s"we can use ${something}"
  }

  //3 - tuples and nested tuples
  val tuple =(1,2)
  val result = tuple match {
    case (1,2) => "exact values"
    case (a,b) => s"we can use ${a}, ${b} here"
  }

  //4 - case classes constructor parameters. This can also be nested
  val list:MyList[Int] = ConsList(1, ConsList(2,Empty))
  list match {
    case Empty => ""
    case ConsList(h,t) => ""
  }

  //5 - list patterns
  val standardList = List(1,2,3)
  standardList match {
    case List(1,_,_) => //extractor - advanced
    case (List(1, _*)) => //list of arbitrary length
    case 1:: List() => //infix pattern
    case List(1,2) :+ 42 => //infix pattern
  }

  //6 - type specifiers
  val unknown:Any = 4
  unknown match {
    case list: List[Int] => //explicit type specifier
  }

  //7 - name binding
  val nameBindingMatch = list match {
    case nonEmptyList @ ConsList(_,_) =>
    case ConsList(1, insideBinding @ Empty) => //insideBinding
  }

  //8 - multi-pattern
  list match {
    case Empty | ConsList(0,_) => //compound pattern
  }

  //9 - if guards
  list match {
    case ConsList(h, ConsList(elem, _)) if elem % 2 == 0 => // can be also nested
  }

  //JVM tricky thing
  val numbersList = List(1,2,3)
  numbersList match {
    case strings: List[String] => "string"
    // in JVM we have type erasure - so for compliator it is just LIST
    case numbers: List[Number] => "number"
  }

}
