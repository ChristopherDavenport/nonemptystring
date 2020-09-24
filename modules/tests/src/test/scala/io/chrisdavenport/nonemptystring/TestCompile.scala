package io.chrisdavenport.nonemptystring



object TestCompile {
  val base : Option[NonEmptyString] = NonEmptyString.fromString("Hello World!")

  val appended = base.map(_.append(""))
  val prepended = base.map(_.prepend(""))
  val together: Option[NonEmptyString] = base.map(a => a ++ a)
}