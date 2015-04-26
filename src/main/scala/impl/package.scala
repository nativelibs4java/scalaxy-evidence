package scalaxy.annotation

import scala.reflect.macros.blackbox.Context

package object impl
{
  def failIfNoAnnotation[T : c.WeakTypeTag, A : c.WeakTypeTag](c: Context) = {
    import c.universe._

    if (!typeHasAnnotation[T, A](c)) {
      c.error(c.enclosingPosition,
        "annotation ${weakTypeOf[A]} not found on ${weakTypeOf[T]}")
    }

    c.Expr[HasAnnotation[T, A]](q"null")
  }

  def failIfAnnotation[T : c.WeakTypeTag, A : c.WeakTypeTag](c: Context) = {
    import c.universe._

    if (typeHasAnnotation[T, A](c)) {
      c.error(c.enclosingPosition,
        "annotation ${weakTypeOf[A]} found on ${weakTypeOf[T]}")
    }

    c.Expr[HasNoAnnotation[T, A]](q"null")
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
