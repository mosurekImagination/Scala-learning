package akka.persistance

import akka.actor.{ActorLogging, ActorSystem, Props, actorRef2Scala}
import akka.persistance.PersistentActors.Accountant.{Invoice, InvoiceBulk}
import akka.persistence.PersistentActor

import java.util.Date

object PersistentActors extends App {

  object Accountant {
    //COMMAND
    case class Invoice(recipient: String, date: Date, amount: Int)

    case class InvoiceBulk(list: List[Invoice])

    //EVENTS
    case class InvoiceRecorded(id: Int, recipient: String, date: Date, amount: Int)
  }

  class Accountant extends PersistentActor with ActorLogging {

    import Accountant._

    var latestInvoiceId = 0
    var totalAmount = 0

    override def persistenceId: String = "simple-accountant" // best practice - make it unique

    //normal receive
    override def receiveCommand: Receive = {
      case Invoice(recipient, date, amount) =>
        log.info(s"Received invoice for $recipient, $date, $amount")
        //non-blocking call
        // will be executed in some point in the future
        persist(InvoiceRecorded(latestInvoiceId + 1, recipient, date, amount)){ e =>
          // all messages which come between persist Future and callback are STASHED
          //SAFE to access mutable state here
          // akka-persistence guarantees that no other thread is accessing the actor during persistence callback
          latestInvoiceId += 1
          totalAmount += amount

          //correctly identified sender
          sender() ! "PersistenceACK"
          log.info(s"Persisted invoice for $recipient, $date, $amount")

        }
      case InvoiceBulk(invoices) =>
        val invoicesIds = latestInvoiceId to (latestInvoiceId + invoices.size)
        val events = invoicesIds.zip(invoices).map {
          case (id, invoice) => InvoiceRecorded(id, invoice.recipient, invoice.date, invoice.amount)
        }
        log.info("Calling persist all method")
        persistAll(events) { e =>
          log.info(s"Persisted SINGLE invoice $e")
          //callback is executed after each event
          latestInvoiceId += 1
          totalAmount += e.amount
        }
    }

    //handler called on recovery
    override def receiveRecover: Receive = {
      //best practice: follow the logic in the persist steps of receiveCommand
      case InvoiceRecorded(id, _, _, amount) =>
        log.info(s"Recovered invoice with id: $id and amount: $amount")
        latestInvoiceId = id
        totalAmount += amount
    }

    //Persisting failures
    //called if persisting failed
    //actor will be stopped

    // best practive start the actor again after a while with backoff supervisor
    override def onPersistFailure(cause: Throwable, event: Any, seqNr: Long): Unit = {
      log.error(s"Failed to persist $event due to $cause")
      super.onPersistFailure(cause, event, seqNr)
    }

    //called if the journal fails to persist the event,
    // the actor is RESUMED as the whole event was rejected before save so actor is not inconsistent state
    override def onPersistRejected(cause: Throwable, event: Any, seqNr: Long): Unit = {
      log.error(s"Persist rejected $event due to $cause")
      super.onPersistRejected(cause, event, seqNr)
    }

  }

  val system = ActorSystem("PersistentActors")
  val accountant = system.actorOf(Props[Accountant](), "simpleAccountant")


  //  accountant ! Invoice("Some company", Date(), 1000)
  //  accountant ! Invoice("Some company 2", Date(), 1000)

  val newInvoices = (1 to 10).map(i=> Invoice(s"$i", Date(), i * 10)).toList
//  accountant ! InvoiceBulk(newInvoices)
  // on restart akka queries all events for actor with specified persistenceID

  // 1. NEVER CALL PERSIST OR PERSISTALL FROM FUTURES

  // 2. Define your own shutdown message as PoisonKill is handled in different mailbox than normal messages
  //    which are stashed between calling persist and callback
}
