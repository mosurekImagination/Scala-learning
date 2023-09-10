package akka.persistance

import akka.persistence.PersistentActor

object MultiplePersists extends App {
  // PERSISTENCE I BASED ON MESSAGE PASSING
  // Order of multiple persist is guaranteed

  case object Event

  case object Event2

  class someActor extends PersistentActor {
    override def receiveRecover: Receive = ???

    override def receiveCommand: Receive = {
      case _ =>
        //events will be persisted in sequence
        //as a result callback also will be executed in sequence
        persist(Event) { e =>
          //callback which will be executed first

          persist(Event) { e =>
            // inside event, will be executed as third
          }
        }
        persist(Event2) { e =>
          //callback which will be executed second


          persist(Event) { e =>
            // inside event, will be executed as fourth          }
          }
        }
    }

    override def persistenceId: String = ???
  }
}
