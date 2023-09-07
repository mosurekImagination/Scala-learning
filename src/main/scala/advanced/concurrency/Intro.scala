package advanced.concurrency

import java.util.concurrent.{Executor, Executors}

object Intro extends App{

  //JVM threads
  val aThread = new Thread(() => println(5))
  val aThread2 = new Thread(() => println(2))

  //Creates completely new OS thread
  aThread.run() //onnly gives a signal to the JVM to start a JVM thread
  aThread.join() // waits for finishing the job

  // different runs produce different results.

  val pool = Executors.newFixedThreadPool(5) //saved threads as  creating them is costly

  

}
