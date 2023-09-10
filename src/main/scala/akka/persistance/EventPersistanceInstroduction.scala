package akka.persistance

object EventPersistanceInstroduction extends App {
  //EventSourcing persistance
  //instead of storing plain target value we store all events

  //pros
  //performance - events are only appended
  //avoids relational stores and ORM
  //full trace of every state
  //fits the akka actor model perfectly

  //cons
  // querying a state potentially expensive - akka persistance query
  // potential poerformance issues with long-live entities - snapshotting
  // data model subject to change - schema evolution
  // just a very different mode


  //Persistent Actors
  // can do everything normal actors can do
  // plus
  //  have persistence ID
  //  persist events to a long-term store
  //  recover state by replaying events from the store

  // when actor handles a command(message)
  //  it can asynchronously persist an event to the store
  //  after the event is persisted, it changes its internal state

  // when actor starts/restarts
  //  it replays all events with its persistence ID
}
