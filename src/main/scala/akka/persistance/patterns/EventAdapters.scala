package akka.persistance.patterns

import akka.persistence.journal.{EventSeq, ReadEventAdapter}

object EventAdapters extends App {

  case object GuitarAdded

  case object GuitarAdded2

  //that needs to be done in conf file

  class GuitarReadEventAdapter extends ReadEventAdapter {
    // journal -> serializer -> read event adapter -> actor
    override def fromJournal(event: Any, manifest: String): EventSeq = event match {
      case GuitarAdded => EventSeq.single(GuitarAdded2)
      case other => EventSeq.single(other)
    }

    // there is also WriteEventAdapter - used for backwards compatibility
    // write -> write event adapter -> serializer -> journal
  }
}
