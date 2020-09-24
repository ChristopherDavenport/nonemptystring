package io.chrisdavenport.nonemptystring

type NonEmptyString = NonEmptyString.Scope.NonEmptyString

object NonEmptyString {

  def fromString(s: String): Option[NonEmptyString] = 
    Scope.NonEmptyString.fromString(s)

  inline def apply(inline s: String) = {
    scala.compiletime.requireConst(s)
    s match {
      case "" => scala.compiletime.error("Empty String Cannot Be Used")
      case _ => Scope.NonEmptyString.fromString(s).get
    }
  }

  object Scope {
    opaque type NonEmptyString = String
    object NonEmptyString {
      def fromString(s: String): Option[NonEmptyString] = 
        if (s.nonEmpty) Some(s)
        else None
    }

    extension (nes: NonEmptyString)
      def toString: String = nes
      def ++(that: NonEmptyString): NonEmptyString = nes + that
      def append(s: String): NonEmptyString = nes + s
      def prepend(s: String): NonEmptyString = s + nes
  }

}
