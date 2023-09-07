package advanced.concurrency

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success}
import scala.concurrent.duration.*
object FuturesAndPromises extends App {

  def meaningOfLife = {
    Thread.sleep(1000)
    println("Meaning of life is 42!")
    42
  }

  val future = Future {
    meaningOfLife
  } // passed implicitly to future object

  future.onComplete { //partial function is used there instead of pattern matching expression
    case Success(value) => println("Meaning of life is received!")
    case Failure(exception) => println(s"Meaning of life failed with: ${exception}")
  } // SOME THREAD

  Thread.sleep(3000)


  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile) =
      println(s"this ${name} poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    val names = Map(
      "1" -> "Mark",
      "2" -> "Tom",
      "3" -> "Karo"
    )
    val friends = Map(
      "1" -> "2"
    )
    val random = new Random()

    def fetchProfile(id: String): Future[Profile] = Future {
      //fetching from db
      Thread.sleep(random.nextInt(500))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      //fetching from db
      Thread.sleep(random.nextInt(500))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  //client
  val mark = SocialNetwork.fetchProfile("1")
  mark.onComplete{
    case Success(markProfile) => {
      val friend = SocialNetwork.fetchBestFriend(markProfile)
      friend.onComplete{
        case Success(friendProfile) => markProfile.poke(friendProfile)
        case Failure(e) => e.printStackTrace()
      }
    }
    case Failure(e) => e.printStackTrace()
  }


  // the solution shown above works, but it is ugly
  // second solution is based on functional composition of the futures
  // map, flatMap, filter
  val name = mark.map(profile => profile.name)
  val bestie = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val filteredFirends = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile)).filter(friend => friend.name.startsWith("T"))

  //for comprehensions
  for {
    mark <- SocialNetwork.fetchProfile("1")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)


  //fallbacks with direct value
  val fallbackProfile = SocialNetwork.fetchProfile("unknown").recover{
    case e: Throwable => Profile("dummyId", "dummyName")
  }

  val fallbackProfileFuture = SocialNetwork.fetchProfile("unknown").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("1") // this is another future
  }

  fallbackProfile.fallbackTo(fallbackProfileFuture)

  Thread.sleep(1000)


  //online baking app
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp{
    val name = "Rock the JVM banking"

    def fetchUser(name: String): Future[User] = Future{
      Thread.sleep(500)
      User(name)
    }
    def createTransaction(user: User, merchantName:String, amount:Double): Future[Transaction] = Future{
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "Success")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      //fetch user
      //create transaction
      // WAIT until it is finished
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      //here you are waiting for future to be completed
      Await.result(transactionStatusFuture, 2.seconds) // implicits conversions -> pimp my library
    }
  }

  println(BankingApp.purchase("Tom", "Phone", "Some Shop", 1000.0))


  //promises
  val promise = Promise[Int] // controller over a future
  val promiseFuture = promise.future

  //thread one - consumer
  promiseFuture.onComplete{
    case Success(r) => println(s"consumer - I've received ${r}")
  }

  //thread 2 - producer
  Thread(()=> {
    Thread.sleep(200)
    println("Fulfilling promise")
    promise.success(42)
    println("Promise fulfilled")
  }).run()

  Thread.sleep(500)
}
