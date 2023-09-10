package akka.persistance.patterns

object DetachedDomainModel extends App {
  //domain model = events our actor thinks it persists
  //data model = objects which actually get persisted

  // good practice: make the two models independent
  // awesome side effect: easier schema evolution

  // by events adapter you manage domain model <=> datamodel transformation
  // changes in schema is extracted to adapter
}
