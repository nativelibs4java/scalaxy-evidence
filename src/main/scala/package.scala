package scalaxy

import scala.language.experimental.macros

package annotation {
  /** Proof that type T has an annotation of type A */
  sealed trait HasAnnotation[T, A]

  /** Proof that type T does not have an annotation of type A */
  sealed trait HasNoAnnotation[T, A]
}

package object annotation {
  implicit def proveHasAnnotation[T, A]: HasAnnotation[T, A] =
    macro impl.failIfNoAnnotation[T, A]

  implicit def proveHasNoAnnotation[T, A]: HasNoAnnotation[T, A] =
    macro impl.failIfAnnotation[T, A]

  def hasAnnotation[T, A]: Boolean =
    macro impl.hasAnnotation[T, A]
}
