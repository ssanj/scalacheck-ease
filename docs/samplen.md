# sampleN

Used for sampling a number of values from a generator that may fail.

__sampleN__ is defined as:

```scala
sampleN[A](n: Int, ga: Gen[A], r: Int): Option[Vector[A]]
```

where __n__ is the number of values required from the generator __ga__ and __r__ is the number of times to retry the generator if it fails to generate a value. If the sample succeeds returns a vector of values of size __n__ within a [Some](http://www.scala-lang.org/api/2.12.3/scala/Some.html). If the generator __ga__ fails more than __r__ times, the result is a [None](http://www.scala-lang.org/api/2.12.3/scala/None$.html).

__sampleN__ has another variant which can be used with generators that don't fail and thus don't need a retry parameter:

```scala
sampleN[A](n: Int, ga: Gen[A]): Option[Vector[A]]
```

Example usage:

```scala
import org.scalacheck._
import net.ssanj.scalacheck.ease.G

G.sampleN(20, Gen.oneOf(Gen.fail, Gen.alphaChar), 100)
res1: Option[List[Char]] = Some(List(k, q, z, i, b, l, j, b, x, v, u, e, X, x, n, j, M, f, a, w))
```

## Why use this?

Something I do often is sample generators to inspect their distribution. ScalaCheck provides us with a couple of functions if we want to get multiple sample values out of a generator:

1. [listOfN](https://github.com/rickynils/scalacheck/blob/1.13.5_sonatype/src/main/scala/org/scalacheck/Gen.scala#L587)
1. [infiniteStream](https://github.com/rickynils/scalacheck/blob/1.13.5_sonatype/src/main/scala/org/scalacheck/Gen.scala#L604)

Both the above functions are defined on the [Gen](https://github.com/rickynils/scalacheck/blob/1.13.5_sonatype/src/main/scala/org/scalacheck/Gen.scala) companion object.

The examples that follow require the following imports:

```scala
import org.scalacheck._
import org.scalacheck.Gen._
import net.ssanj.scalacheck.ease.G
```


### listOfN

is defined as:

```scala
listOfN[T](n: Int, g: Gen[T]): Gen[List[T]]
```

It takes in a number __n__ the represents how many times you want to call the supplied generator __g__ and then returns a generator of a list of values from that generator.

For example to generate ten alphabetic characters we could call it with:

```scala
scala> val g1 = listOfN(10, alphaChar)
g1: org.scalacheck.Gen[List[Char]] = org.scalacheck.Gen$$anon$1@3a4bd704

scala> g1.sample
res0: Option[List[Char]] = Some(List(c, x, p, j, f, n, j, l, a, m))
```

Now let's look what how this function behaves when it is given a generator that may fail:

```scala
scala> val flakeyGen: Gen[Char] = oneOf(fail, alphaChar)
flakeyGen: org.scalacheck.Gen[Char] = org.scalacheck.Gen$$anon$1@2de9e4c3

scala> val g2 = listOfN(10, flakeyGen)
g2: org.scalacheck.Gen[List[Char]] = org.scalacheck.Gen$$anon$1@1067688b

scala> g2.sample
res1: Option[List[Char]] = None
```

If the generator fails to generate a single value, all the generated values are discarded and a __None__ is returned. Wouldn't it be nice if __listOfN__ skipped the failed generations and returned a list of valid values?

### infiniteStream

is defined as:

```scala
infiniteStream[T](g: ⇒ Gen[T]): Gen[Stream[T]]
```

It takes a generator __g__ of some type and returns a generator of a stream of values of that type.

For example to generate ten alphabetic characters we could call it with:

```scala
scala> val g3 = infiniteStream(alphaChar)
g3: org.scalacheck.Gen[Stream[Char]] = org.scalacheck.Gen$$anon$3@316f0813

scala> g3.sample.map(_.take(10).toList)
res2: Option[List[Char]] = Some(List(j, g, c, h, w, j, i, C, h, i))
```

Now let's look what how this function behaves when we give it the __flakeyGen__ from the previous example:

```scala
scala> val g4 = infiniteStream(flakeyGen)
g4: org.scalacheck.Gen[Stream[Char]] = org.scalacheck.Gen$$anon$3@13171942

scala> g4.sample.map(_.take(10).toList)
org.scalacheck.Gen$RetrievalError: couldn't generate value
  at org.scalacheck.Gen.loop$1(Gen.scala:57)
  at org.scalacheck.Gen.doPureApply(Gen.scala:58)
  at org.scalacheck.Gen.pureApply(Gen.scala:72)
  at org.scalacheck.Gen$$anon$11.$anonfun$result$1(Gen.scala:611)
  at org.scalacheck.Gen$.org$scalacheck$Gen$$unfold$1(Gen.scala:605)
  at org.scalacheck.Gen$.$anonfun$infiniteStream$1(Gen.scala:606)
  at scala.collection.immutable.Stream$Cons.tail(Stream.scala:1169)
  at scala.collection.immutable.Stream$Cons.tail(Stream.scala:1159)
  at scala.collection.immutable.Stream.$anonfun$take$2(Stream.scala:789)
  at scala.collection.immutable.Stream$Cons.tail(Stream.scala:1169)
  at scala.collection.immutable.Stream$Cons.tail(Stream.scala:1159)
  at scala.collection.generic.Growable.loop$1(Growable.scala:54)
  at scala.collection.generic.Growable.$plus$plus$eq(Growable.scala:58)
  at scala.collection.generic.Growable.$plus$plus$eq$(Growable.scala:50)
  at scala.collection.mutable.ListBuffer.$plus$plus$eq(ListBuffer.scala:186)
  at scala.collection.mutable.ListBuffer.$plus$plus$eq(ListBuffer.scala:44)
  at scala.collection.TraversableLike.to(TraversableLike.scala:590)
  at scala.collection.TraversableLike.to$(TraversableLike.scala:587)
  at scala.collection.AbstractTraversable.to(Traversable.scala:104)
  at scala.collection.TraversableOnce.toList(TraversableOnce.scala:294)
  at scala.collection.TraversableOnce.toList$(TraversableOnce.scala:294)
  at scala.collection.AbstractTraversable.toList(Traversable.scala:104)
  at .$anonfun$res7$1(<console>:22)
  at scala.Option.map(Option.scala:146)
  ... 39 elided
```

This function is unsafe and throws an exception if the generator fails when generating a single value. Wouldn't it be nice if this method was safe and returned us a stream of valid values?

This is where [__sampleN__](https://github.com/ssanj/scalacheck-ease/blob/master/src/main/scala/net/ssanj/scalacheck/ease/G.scala#L37) comes in. __sampleN__ can be used with the above examples as:

```scala
scala> G.sampleN(10, alphaChar)
res9: Option[Vector[Char]] = Some(Vector(p, h, d, y, x, j, n, f, a, x))

scala> G.sampleN(10, flakeyGen, 100)
res10: Option[Vector[Char]] = Some(Vector(l, q, u, h, n, m, x, d, l, u))
```

__sampleN__ is defined as:

```scala
sampleN[A](n: Int, ga: Gen[A], r: Int): Option[Vector[A]]
```

where __n__ is the number of values required from the generator __ga__ and __r__ is the number of times to retry the generator if it fails to generate a value.

So what happens when the generator can't generate __n__ valid values even with the __r__ retries? Let's have a look:

```scala
scala> G.sampleN(10, fail)
res13: Option[Vector[Nothing]] = None
```

It returns a __None__ without throwing an exception and therefore can be used safely.

__sampleN__ has another variant which can be used with generators that don't fail and thus don't need a retry parameter:

```scala
sampleN[A](n: Int, ga: Gen[A]): Option[Vector[A]]
```

For example:

```scala
scala> G.sampleN(10, alphaChar)
res14: Option[Vector[Char]] = Some(Vector(u, z, i, V, d, p, e, n, m, r))
```