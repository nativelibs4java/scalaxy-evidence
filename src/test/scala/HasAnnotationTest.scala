package scalaxy.annotation
package test

import org.scalamock.scalatest.MockFactory
import org.scalatest.{ FlatSpecLike, Matchers }

class HasAnnotationTest extends FlatSpecLike with Matchers with MockFactory {

  behavior of "hasAnnotation"

  it should "detect presence of static annotations" in {
    hasAnnotation[WithMyAnnotation, MyAnnotation] should equal(true)
    hasAnnotation[WithoutMyAnnotation, MyAnnotation] should equal(false)
  }
}
