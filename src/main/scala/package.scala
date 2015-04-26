package scalaxy

import scala.language.experimental.macros

package annotation
{
  /** Evidence that T has an annotation of type A */
  sealed trait HasAnnotation[T, A]

  /** Evidence that A is not proved */
  sealed trait ![A]
}

package object annotation
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
