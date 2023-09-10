package akka.persistance

import akka.serialization.Serializer

object Serialization extends App {

  //serializer gets JVM objects and converts them in a format that they will be stored in database
  // and in the other direction
  class CustomSerializer extends Serializer{
    override def identifier: Int = 1234

    override def toBinary(o: AnyRef): Array[Byte] = ???

    override def includeManifest: Boolean = ???

    override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = ???
  }

}
