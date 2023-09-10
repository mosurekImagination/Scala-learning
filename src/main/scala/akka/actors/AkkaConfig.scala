package akka.actors

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object AkkaConfig extends App {

  // 1 - inline configuration
  val configString ="""
      | akka {
      |   loglevel = "DEBUG
      | }
      |""".stripMargin

    val config = ConfigFactory.parseString(configString)
    val system = ActorSystem("system", config)

  val defaultConfigFileSystem = ActorSystem("asdf")
  //by default akka reads configuration from file
    //val config2 = defaultConfigFileSystem.actorOf

    //custom config in the same default file
    val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
    specialConfig.getString("akka.loglevel")

    //we can also have different file and different types like .properties .json .yaml

}
