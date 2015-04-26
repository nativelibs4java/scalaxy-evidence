package scalaxy.evidence
package test

import org.scalamock.scalatest.MockFactory
import org.scalatest.{ FlatSpecLike, Matchers }

import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBoxError

class ProofsTest extends FlatSpecLike with Matchers with MockFactory {

  behavior of "scalaxy.evidence on complex predicates"

  val decls = q"""
    import scalaxy.evidence._

    type IsIntegralWithAnds[A] =
      A <:< AnyVal &&
      ![A =:= Double] &&
      ![A =:= Float] &&
      ![A =:= Boolean] &&
      ![A =:= Char]

    type IsIntegralWithOrs[A] =
      (A =:= Int) ||
      (A =:= Short) ||
      (A =:= Byte) ||
      (A =:= Long)

    def takeIntegralWithAnds[A : IsIntegralWithAnds] = ???
    def takeIntegralWithOrs[A : IsIntegralWithOrs] = ???
  """

  val integrals = Seq(
    ("Int", tq"Int"),
    ("Long", tq"Long"),
    ("Short", tq"Short"),
    ("Byte", tq"Byte"))

  val nonIntegrals = Seq(
    ("Double", tq"Double"),
    ("Boolean", tq"Boolean"),
    ("Char", tq"Char"),
    ("String", tq"String"))

  for ((name, tpe) <- integrals) {
    it should s"prove $name is integral with &&" in {
      toolbox.compile(q"..$decls ; takeIntegralWithAnds[$tpe]")
    }
    it should s"prove $name is integral with ||" in {
      toolbox.compile(q"..$decls ; takeIntegralWithOrs[$tpe]")
    }
  }

  for ((name, tpe) <- nonIntegrals) {
    it should s"prove $name is not integral with &&" in {
      a [ToolBoxError] should be thrownBy {
        toolbox.compile(q"..$decls ; takeIntegralWithAnds[$tpe]")
      }
    }
    it should s"prove $name is not integral with ||" in {
      a [ToolBoxError] should be thrownBy {
        toolbox.compile(q"..$decls ; takeIntegralWithOrs[$tpe]")
      }
    }
  }
}
