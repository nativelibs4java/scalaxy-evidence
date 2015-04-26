[![Build Status](https://travis-ci.org/nativelibs4java/scalaxy-annotation.svg?branch=master)](https://travis-ci.org/nativelibs4java/scalaxy-annotation)

Simple typeclasses that check that a given annotation is set (or not) on a given class.

For instance, take `javax.persistence.Entity`: it is set on classes that are JPA entities. A sensible `persist[T]` method should not accept types `T` which do not have an `@Entity` annotation. Also, one may not want to persist entities marked with the `@Deprecated` annotation (which is not retained at runtime).

This is where this library can help you:

```scala
  import javax.persistence.Entity
  import scalaxy.annotation._

  type IsEntity[T] = HasAnnotation[T, Entity]

  type IsNotDeprecated[T] = HasNoAnnotation[T, Deprecated]

  def persist[T : IsEntity : IsNotDeprecated](t: T) = ???
```

These contracts are simple: `persist[T]` requires the static proof that `T` is annotated with `@Entity` and *not* with `@Deprecated`.

Now if you take the following code:

```scala

@Entity(name = "...")
class GoodEntity

@Entity(name = "...") @Deprecated
class DeprecatedEntity

class NotAnEntity

serialize(new GoodEntity)        // Ok.
serialize(new DeprecatedEntity)  // Error: failed to prove IsNotDeprecated
serialize(new NotAnEntity)       // Error: failed to prove IsEntity
```
