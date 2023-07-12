package lectures.functional

object OptionsApp extends App {

  //options are wrappers for values which might be present or not
  //Option is heavily used in Scala SDK

  val option = Some(5)
  val option2 = None

  //they deal with unsafe APIs

  def unsafeMethod():String = null
  Some(unsafeMethod()) // wrong
  Option(unsafeMethod()) //correct
  //apply function will instantiate Some or None accordingly

  def safeMethod():String = "Something"
  Option(unsafeMethod()).orElse(Option(safeMethod()))

  //if you design unsafe APIs - wrapp it with Option to ease usage
  val value = betterUnsafeMethod orElse betterBackupMethod()
  def betterUnsafeMethod(): Option[String] = None
  def betterBackupMethod(): Option[String] = Option("Some")
  println(value)

  option.isEmpty
  option.get //unsafe

  //hof
  option.map(_ * 2)


}
