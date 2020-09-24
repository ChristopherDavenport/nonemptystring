package io.chrisdavenport.nonemptystring

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

final class NonEmptyString private (override val toString: String) extends AnyVal{
  def ++(other: NonEmptyString): NonEmptyString = new NonEmptyString(toString + other.toString)
  def append(other: String): NonEmptyString = new NonEmptyString(toString + other)
  def prepend(other: String): NonEmptyString = new NonEmptyString(other + toString)
}

object NonEmptyString {


  def fromString(s: String): Option[NonEmptyString] = 
    if (s.nonEmpty) Some(new NonEmptyString(s))
    else None

  def apply(s: String): NonEmptyString = macro Macros.nonEmptyLiteral

  private class Macros(val c: blackbox.Context) {
    import c.universe._
    def nonEmptyLiteral(s: c.Expr[String]): c.Expr[NonEmptyString] =
      s.tree match {
        case Literal(Constant(s: String))=>
            fromString(s)
            .fold(
              c.abort(c.enclosingPosition, "String Literal is Empty")
            )(
              _ => c.Expr(q"""
                @SuppressWarnings(Array("org.wartremover.warts.Throw"))
                val ne = _root_.io.chrisdavenport.nonemptystring.NonEmptyString.fromString($s).get
                ne
              """)
            )
        case _ =>
          c.abort(
            c.enclosingPosition,
            s"This method uses a macro to verify that a NonEmptyString literal is valid. Use NonEmptyString.fromString if you have a dynamic value you want to parse as an NonEmptyString."
          )
      }
  }

  /*
  implicit class NonEmptyHelper(val sc: StringContext) extends AnyVal {
    // TODO: DO THIS WITH MACRO AND DON'T CHEAT
    def nonempty(args: Any*): NonEmptyString = {
      val somethingNonEmpty = sc.parts.toList.exists(_.nonEmpty)
      if (somethingNonEmpty){
        val strings = sc.parts.iterator
        val expressions = args.iterator
        val buf = new StringBuilder(strings.next())
        while(strings.hasNext) {
          buf.append(expressions.next())
          buf.append(strings.next())
        }
        new NonEmptyString(buf.toString())
      } else ???
    }
  }
  */
}