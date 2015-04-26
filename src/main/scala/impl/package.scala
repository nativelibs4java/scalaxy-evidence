package scalaxy.annotation

import scala.reflect.macros.blackbox.Context
import scala.reflect.macros.TypecheckException

package object impl
{
  def proveHasAnnotation[T : c.WeakTypeTag, A : c.WeakTypeTag](c: Context) = {
    import c.universe._

    println(s"proveHasAnnotation[${weakTypeOf[A]}, ${weakTypeOf[T]}]")

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
