package scalaxy.annotation
package test

import org.scalamock.scalatest.MockFactory
import org.scalatest.{ FlatSpecLike, Matchers }

import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBoxError

class ProofsTest extends FlatSpecLike with Matchers with MockFactory {

  behavior of "scalaxy.annotation"

  val decls = q"""
    class MyAnnotation extends scala.annotation.StaticAnnotation

    @MyAnnotation
    class WithMyAnnotation

    class WithoutMyAnnotation
  """

  it should "prove HasAnnotation on annotated types" in {
    toolbox.compile(q"""
      import scalaxy.annotation._
      ..$decls
      implicitly[HasAnnotation[WithMyAnnotation, MyAnnotation]]
    """)
  }

  it should "disprove HasAnnotation on types with no annotation" in {
    a [ToolBoxError] should be thrownBy {
      toolbox.compile(q"""
        import scalaxy.annotation._
        ..$decls
        implicitly[HasAnnotation[WithoutMyAnnotation, MyAnnotation]]
      """)
    }
  }

  it should "prove HasNoAnnotation on types with no annotation" in {
    toolbox.compile(q"""
      import scalaxy.annotation._
      ..$decls
      implicitly[HasNoAnnotation[WithoutMyAnnotation, MyAnnotation]]
    """)
  }

  it should "disprove HasNoAnnotation on annotated types" in {
    a [ToolBoxError] should be thrownBy {
      toolbox.compile(q"""
        import scalaxy.annotation._
        ..$decls
        implicitly[HasNoAnnotation[WithMyAnnotation, MyAnnotation]]
      """)
    }
  }
}
