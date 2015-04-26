package scalaxy

import scala.annotation.implicitNotFound
import scala.language.experimental.macros

import scala.util.Either

package evidence
{
  /** Evidence that T has an annotation of type A */
  @implicitNotFound(msg = "${T} does not have an annotation of type ${A}.")
  sealed trait HasAnnotation[T, A]

  /** Evidence that A is not proved */
  @implicitNotFound(msg = "Failed to prove negation: ${A} is proved.")
  sealed trait ![A]

  /**
   * Evidence that A or B is proved (eager: tries to prove B even if A is proved).
   *
   * Note: we could use scala.util.Either, but it messes up the type resolution
   * of A and B (and would not allow full resolution of A and B when they're both present).
   */
  @implicitNotFound(msg = "Failed to prove disjunction (OR) of ${A} and ${B}")
  case class ||[A, B](left: Option[A], right: Option[B])

  /**
   * Evidence that one and only one of A and B is proved (XOR).
   */
  @implicitNotFound(msg = "Failed to prove disjunction (OR) of ${A} and ${B}")
  case class ^^[A, B](left: Option[A], right: Option[B])
}

package object evidence
{
  @implicitNotFound(msg = "Failed to prove conjunction (AND) of ${A} and ${B}")
  type &&[A, B] = (A, B)

  /** Proof that type T does not have an annotation of type A */
  type HasNoAnnotation[T, A] = ![HasAnnotation[T, A]]

  def hasAnnotation[T, A]: Boolean =
    macro impl.hasAnnotation[T, A]

  implicit def proveHasAnnotation[T, A]: HasAnnotation[T, A] =
    macro impl.proveHasAnnotation[T, A]

  implicit def proveNot[T]: ![T] =
    macro impl.proveNot[T]

  implicit def proveAnd[A, B](implicit left: A, right: B): &&[A, B] =
    (left, right)

  implicit def proveOr[A, B]: ||[A, B] =
    macro impl.proveOr[A, B]

  implicit def proveXor[A, B]: ^^[A, B] =
    macro impl.proveXor[A, B]
}
