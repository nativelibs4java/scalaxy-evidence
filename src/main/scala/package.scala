package scalaxy

import scala.annotation.implicitNotFound
import scala.language.experimental.macros

package evidence
{
  /** Evidence that T has an annotation of type A */
  @implicitNotFound(msg = "${T} does not have an annotation of type ${A}.")
  sealed trait HasAnnotation[T, A]

  /** Evidence that A is not proved */
  @implicitNotFound(msg = "Failed to prove negation: ${A} is proved.")
  sealed trait ![A]
}

package object evidence
{
  /** Proof that type T does not have an annotation of type A */
  type HasNoAnnotation[T, A] = ![HasAnnotation[T, A]]

  def hasAnnotation[T, A]: Boolean =
    macro impl.hasAnnotation[T, A]

  implicit def proveHasAnnotation[T, A]: HasAnnotation[T, A] =
    macro impl.proveHasAnnotation[T, A]

  implicit def proveNot[T]: ![T] =
    macro impl.proveNot[T]
}
