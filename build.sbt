name := "Scala-learning"

version := "0.1"

scalaVersion := "3.3.0"

val akkaVersion = "2.7.0"
//db
val leveldbVersion = "0.7"
val leveldbjniVersion = "1.8"
val postgresVersion = "42.2.2"
val cassandraVersion = "1.1.1"
val json4sVersion = "3.2.11"
val protobufVersion = "3.6.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.2.10",

  //local db
  "org.iq80.leveldb" % "leveldb" % leveldbVersion,
  "org.fusesource.leveldbjni" % "leveldbjni-all" % leveldbjniVersion,

  //jdbc with postgresql
  "org.postgresql" % "postgresql" % postgresVersion,
//  "com.github.dnvriend" %% "akka-persistance-jdbc" % "3.5.3",

  // cassandra
  "com.typesafe.akka" %% "akka-persistence-cassandra" % cassandraVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % cassandraVersion % Test,

  // google protocol buffers
  "com.google.protobuf" % "protobuf-java" % protobufVersion
  ,

)