package scalaxy.evidence

package test
{
  class MyAnnotation extends scala.annotation.StaticAnnotation

  @MyAnnotation
  class WithMyAnnotation

  class WithoutMyAnnotation
}

package object test
{
  import scala.reflect.runtime.currentMirror
  import scala.tools.reflect.ToolBox

  lazy val toolbox = currentMirror.mkToolBox()
}
