Actor model principles
- every interaction based on sending messages
- full actor encapsulation
- no locking
- message-sending latency
- at most once message delivery
- message ordering maintained per sender/receiver pair

The principles applies
- on the same JVM in a parallel application
- multiple JVM in the same machine
- multiple JVMs in multiple machines.

Akka was designed with the distributed goal in mind

In akka we always work with actor reference -- the real actor can be anywhere
We don't need to know where the actor is

Location transparency != transparent remoting
- location transparency = we don't care where the object is
- transparent remoting = we're using the object as if it were local