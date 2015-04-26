package scalaxy.annotation
package test

import org.scalamock.scalatest.MockFactory
import org.scalatest.{ FlatSpecLike, Matchers }

import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBoxError

class PersistenceExampleTest extends FlatSpecLike with Matchers with MockFactory {

  behavior of "scalaxy.annotation on java annotations"

  val decls = q"""
    import javax.persistence.Entity
    import scalaxy.annotation._

    type IsEntity[T] = HasAnnotation[T, Entity]

    type IsNotDeprecated[T] = ![HasAnnotation[T, Deprecated]]

    def serialize[T : IsEntity : IsNotDeprecated](t: T) = ???
  """

  it should "allow persistence of GoodEntity" in {
    toolbox.compile(q"""
      ..$decls

      @Entity(name = "GoodEntity")
      class GoodEntity

      serialize(new GoodEntity)
    """)
  }

  it should "forbid persistence of DeprecatedEntity" in {
    a [ToolBoxError] should be thrownBy {
      toolbox.compile(q"""
        @Entity(name = "DeprecatedEntity") @Deprecated
        class DeprecatedEntity

        serialize(new DeprecatedEntity)
      """)
    }
  }

  it should "forbid persistence of NotAnEntity" in {
    a [ToolBoxError] should be thrownBy {
      toolbox.compile(q"""
        class NotAnEntity

        serialize(new NotAnEntity)
      """)
    }
  }
}
