package advanced.concurrency

object JVMConcurrency extends App {

  def runInParallel(): Unit = {
    var x = 0
    val thread1 = new Thread(() => {
      x =1
    })
    val thread2 = new Thread(() => {
      x =2
    })

    thread1.start()
    thread2.start()

    println(x) //race condition
  }

  // only AnyRefs can have synchronized blocks
  this.synchronized{

    this.wait() // release the lock and wait when allowed to proceed, lock the monitor again and continue
    this.notify() // signal one sleeping thread they might continue
    this.notifyAll() // signal all sleeping threads
  }

  // make no assumptions about who gets the lock first
  // keep locking to a minimum
  // maintain thread safety at all times in parallel applications

  //wait() notify()
  runInParallel()
}
