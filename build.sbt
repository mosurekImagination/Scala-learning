name := "Scala-learning"

version := "0.1"

scalaVersion := "3.3.0"

val akkaVersion = "2.7.0"
val akkaHttpVersion = "10.5.2"
//db
val leveldbVersion = "0.7"
val leveldbjniVersion = "1.8"
val postgresVersion = "42.2.2"
val cassandraVersion = "1.1.1"
val json4sVersion = "3.2.11"
val protobufVersion = "3.6.1"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")


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
  "com.google.protobuf" % "protobuf-java" % protobufVersion,

  //remoting and clustering
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,


  "io.aeron" % "aeron-driver" % "1.40.0",
  "io.aeron" % "aeron-client" % "1.40.0",


  //http - microservices
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",


)