package io.chrisdavenport.nonemptystring


type NonEmptyString = NonEmptyString.Scope.NonEmptyString

object NonEmptyString {

  def fromString(s: String): Option[NonEmptyString] = 
    Scope.NonEmptyString.fromString(s)

  inline def apply(inline s: String) =
    scala.compiletime.requireConst(s)
    if (s == "") scala.compiletime.error("Empty String Cannot Be Used")
    else Scope.NonEmptyString.fromString(s).get

  object Scope {
    opaque type NonEmptyString = String
    object NonEmptyString {
      def fromString(s: String): Option[NonEmptyString] = 
        if (s.nonEmpty) Some(new NonEmptyString(s))
        else None
    }

    extension (nes: NonEmptyString)
      def toString: String = nes
      def ++(that: NonEmptyString): NonEmptyString = nes + that
      def append(s: String): NonEmptyString = nes + s
      def prepend(s: String): NonEmptyString = s + nes
  }

  // https://github.com/Comcast/ip4s/blob/main/shared/src/main/scala-3/Interpolators.scala
  // Dotty Compile

}
