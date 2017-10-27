package net.ssanj.scalacheck.ease

import org.scalacheck._
import syntax._

object UniqueListNSized {

  private def assertLength[A]: ((List[A], Int)) => Prop = { case (values, size) => values.length == size }

  private def assertUniqueValues[A]: List[A] => Prop = values => values.toSet.size == values.length

  private def seed: rng.Seed = rng.Seed(Gen.choose(1000000000L, 2000000000L).sample.getOrElse(Int.MaxValue))

  private def isEqual[A]: Predicate[A] = _ == _

  private def resize: Resize = identity

  private def failingGen[A: Arb]: Gen[A] = Gen.oneOf(arb[A], Gen.fail[A])

  def containerWithResizeArbProp[A: Arb](f: Resize => Predicate[A] => Gen[(List[A], Int)]): Prop =
    Prop.forAll(f(resize)(isEqual[A])) { assertLength }

  def containerWithResizeGenProp[A: Arb](f: (Gen[A], Resize) => Predicate[A] => Gen[(List[A], Int)]): Prop =
    Prop.forAll(f(arb[A], resize)(isEqual[A])) { assertLength }

  def containerWithSizeArbProp[A: Arb](f: Predicate[A] => Gen[(List[A], Int)]): Prop =
    Prop.forAll(f(isEqual[A])) { assertLength }

  def containerWithSizeGenProp[A: Arb](f: Gen[A] => Predicate[A] => Gen[(List[A], Int)]): Prop =
    Prop.forAll(f(arb[A])(isEqual[A])) { assertLength }

  def containerWithSizedSeedArbProp[A: Arb](f: (Int, Int) => (Predicate[A], rng.Seed) => Gen[List[A]]): Prop =
    Prop.forAll(Gen.sized(size => f(size, size * 2)(isEqual[A], seed))) { assertUniqueValues }

  def containerWithSizedSeedGenProp[A: Arb](f: (Int, Gen[A], Int) => (Predicate[A], rng.Seed) => Gen[List[A]]): Prop =
    Prop.forAll(Gen.sized(size => f(size, arb[A], size * 2)(isEqual[A], seed))) { assertUniqueValues }

  def containerWithUniqueValuesArbProp[A: Arb](f: Predicate[A] =>  Gen[List[A]]): Prop =
    Prop.forAll(f(isEqual[A])) { assertUniqueValues }

  def containerWithUniqueValuesGenProp[A: Arb](f: Gen[A] => Predicate[A] => Gen[List[A]]): Prop =
    Prop.forAll(f(arb[A])(isEqual[A])) { assertUniqueValues }

  def containerWithUniqueValuesWithNArbProp[A: Arb](f: (Int, Int) => Predicate[A] => Gen[List[A]]): Prop =
    Prop.forAll(Gen.sized(size => f(size, size * 2)(isEqual[A]))) { assertUniqueValues }

  //TODO: How can we make this test better?
  //TODO: We only want to fail when the Generator fails not when the value is not unique.
  def containerWithUniqueValuesWithNGenFailingProp[A: Arb](f: (Int, Gen[A], Int) => Predicate[A] => Gen[List[A]]): Prop =
    Prop.forAll(Gen.choose(20, 100)) { n =>
      f(n, failingGen[A], n * 10)(isEqual[A]) == Gen.fail
    }

  def containerWithUniqueValuesWithNGenProp[A: Arb](f: (Int, Gen[A], Int) => Predicate[A] => Gen[List[A]]): Prop =
    Prop.forAll(Gen.sized(size => f(size, arb[A], size * 2)(isEqual[A]))) { assertUniqueValues }

}