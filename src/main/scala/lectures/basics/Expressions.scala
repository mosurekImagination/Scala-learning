package lectures.basics

object Expressions extends App {

  val x = 1 + 2 //EXPRESSION -> evaluated to the value
  print(x)

  //Instruction vs Expression
  //Instruction (DO) - imperative style
  //Expression (VALUE) - everything returns a value - functional way
  val value = if(true) 5 else 3 // instead of DO something you return something
  //IF EXPRESSION not INSTRUCTION


  //LOPS like while/for are bounded to imperative style

  //UNIT === void
  //UNIT === ()
  //side effects println(), whiles(), reassigning => side effects => returning unit

  val aCodeBlock = {
    if(true) "something" else "somethingElse"
  } //this is also expression; value is the last expression

  //instructions are executed, expressions are evaluated
}
