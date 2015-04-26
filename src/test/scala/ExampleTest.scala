package scalaxy.annotation
package test

import org.scalamock.scalatest.MockFactory
import org.scalatest.{ FlatSpecLike, Matchers }

import scala.reflect.runtime.universe._
import scala.tools.reflect.ToolBoxError

class ExampleTest extends FlatSpecLike with Matchers with MockFactory {

  behavior of "scalaxy.annotation on java annotations"

  val decls = q"""
    import javax.persistence.Entity
    import scalaxy.annotation._

    type IsEntity[T] = HasAnnotation[T, Entity]

    type IsNotDeprecated[T] = HasNoAnnotation[T, Deprecated]

    def serialize[T : IsEntity : IsNotDeprecated](t: T) = ???
  """

  it should "allow serialization of NewEntity" in {
    toolbox.compile(q"""
      ..$decls

      @Entity(name = "NewEntity")
      class NewEntity

      serialize(new NewEntity)
    """)
  }

  it should "forbid serialization of OldEntity" in {
    a [ToolBoxError] should be thrownBy {
      toolbox.compile(q"""
        @Entity(name = "OldEntity") @Deprecated
        class OldEntity

        serialize(new OldEntity)
      """)
    }
  }

  it should "forbid serialization of NotAnEntity" in {
    a [ToolBoxError] should be thrownBy {
      toolbox.compile(q"""
        class NotAnEntity

        serialize(new NotAnEntity)
      """)
    }
  }
}
