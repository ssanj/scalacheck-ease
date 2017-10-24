package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._
import syntax._

object UniqueListNSeedProps extends Properties("uniqueListNSeed") {

  private def assertUniqueValues[A]: List[A] => Prop = values => values.toSet.size == values.length

  private def isEqual[A]: (A , A) => Boolean = _ == _

  private def seed: rng.Seed = rng.Seed(Gen.choose(1000000000L, 2000000000L).sample.getOrElse(Int.MaxValue))

  private def containerWithSizeArbProp[A: Arb] = Prop.forAll(Gen.sized(size => G.uniqueListNSeed[A](size, size * 2)(isEqual[A], seed))) { assertUniqueValues }

  private def containerWithSizeGenProp[A](ga: Gen[A]) = Prop.forAll(Gen.sized(size => G.uniqueListNSeed[A](size, ga, size * 2)(isEqual[A], seed))) { assertUniqueValues }

  property("should always return Int containers of the required size with Arb") =
      containerWithSizeArbProp[Int]

  property("should always return Char containers of the required size with Arb") =
    containerWithSizeArbProp[Char]

  property("should always return AnyVal containers of the required size with Arb") =
    containerWithSizeArbProp[AnyVal]

  property("should always return Int containers of the required size with Gen") =
    containerWithSizeGenProp(arb[Int])

  property("should always return Char containers of the required size with Gen") =
    containerWithSizeGenProp(arb[Char])

  property("should always return AnyVal containers of the required size with Gen") =
    containerWithSizeGenProp(arb[AnyVal])

}
