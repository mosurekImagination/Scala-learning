package akka.persistance

object Snapshots extends App {

  // snapshots - do not recover the whole history, but recover since latest snapshot
  //  we can save snapshot with
  //  saveSnapshot(object) method - ASYNCHORNOUS method, like persisting - like sending message to snapshot actor
        // then we receive snapshotSuccess(metadata) or snapshotFailure(metadata, reason) objects
  //  then in recover we have additional message SnapshotOffer(metadata, contents) which we can use to set internal state
}
