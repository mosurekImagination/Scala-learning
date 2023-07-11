package lectures.oop

object Exception extends App {

  //throwable classes extend Throwable
  // 2 major throwable classes are Exception and Error
  // Exception - something went wrong with the application
  // Error - something went from with the system (JVM) - overflow

  //exceptions comes from Java world. They are JVM related stuff - not Java/Scala related
  val potentialFail = try {
    throw new RuntimeException("Some exception")
  } catch {
    case e: RuntimeException => println("Caught exception")
  } finally {
    //optional
    //
    println("Finally block")
  }

}
