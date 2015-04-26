package scalaxy.evidence

import scala.reflect.macros.blackbox.Context
import scala.reflect.macros.TypecheckException

package object impl
{
  def proveHasAnnotation[T : c.WeakTypeTag, A : c.WeakTypeTag](c: Context) = {
    import c.universe._

    if (!typeHasAnnotation[T, A](c)) {
      c.error(c.enclosingPosition,
        s"annotation ${weakTypeOf[A]} not found on ${weakTypeOf[T]}")
    }

    c.Expr[HasAnnotation[T, A]](q"null")
  }

  def proveNot[T : c.WeakTypeTag](c: Context) = {
    import c.universe._

    try {
      val res = c.typecheck(c.inferImplicitValue(pt = weakTypeOf[T], silent = false))
      c.error(c.enclosingPosition,
        s"Failed to prove that ${weakTypeOf[T]} is not proved: $res")
    } catch { case ex: TypecheckException => }

    c.Expr[![T]](q"null")
  }

  private[this]
  def proveGenericOr[A : c.WeakTypeTag, B : c.WeakTypeTag, T : c.WeakTypeTag]
                    (c: Context)(isExclusive: Boolean, builder: c.Tree) = {
    import c.universe._

    def tryInfer(tpe: Type): Option[Tree] =
      try {
        val res = Some(c.typecheck(c.inferImplicitValue(pt = tpe, silent = false)))
        res
      } catch { case exLeft: TypecheckException =>
        None
      }

    val results = Seq(weakTypeOf[A], weakTypeOf[B]).map(tryInfer(_))

    results match {
      case Seq(None, None) => 
        c.error(c.enclosingPosition,
          s"Neither ${weakTypeOf[A]} nor ${weakTypeOf[B]} were proved")
      case Seq(None, Some(_)) | Seq(Some(_), None) =>
        // XOR = true
      case Seq(Some(_), Some(_)) if isExclusive => 
        c.error(c.enclosingPosition,
          s"Both ${weakTypeOf[A]} and ${weakTypeOf[B]} were proved")
      case _ => 
        // OR = true
    }

    val Seq(left, right) = results.map {
      case Some(v) => q"Some($v)"
      case _ => q"None"
    }
    c.Expr[T](q"$builder[${weakTypeOf[A]}, ${weakTypeOf[B]}]($left, $right)")
  }

  def proveOr[A : c.WeakTypeTag, B : c.WeakTypeTag](c: Context) = {
    import c.universe._
    proveGenericOr[A, B, A || B](c)(isExclusive = false, q"scalaxy.evidence.||")
  }

  def proveXor[A : c.WeakTypeTag, B : c.WeakTypeTag](c: Context) = {
    import c.universe._
    proveGenericOr[A, B, A ^^ B](c)(isExclusive = true, q"scalaxy.evidence.^^")
  }

  def hasAnnotation[T : c.WeakTypeTag, A : c.WeakTypeTag](c: Context) = {
    import c.universe._

    c.Expr[Boolean](q"${typeHasAnnotation[T, A](c)}")
  }

  private[this]
  def typeHasAnnotation[T : c.WeakTypeTag, A : c.WeakTypeTag](c: Context): Boolean = {
    import c.universe._

    val tpe = weakTypeOf[T]
    val annotationTpe = weakTypeOf[A]

    tpe.typeSymbol.asType.annotations
      .exists(a => a.tree.tpe <:< annotationTpe)
  }
}
