package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._
import syntax._

object UniqueListNProps extends Properties("uniqueListN") {

  private def assertUniqueValues[A]: List[A] => Prop = values => values.toSet.size == values.length

  private def isEqual[A]: (A , A) => Boolean = _ == _

  private def containerWithUniqueValuesArbProp[A: Arb]= Prop.forAll(G.uniqueListN[A](isEqual[A])) { assertUniqueValues }

  private def containerWithUniqueValuesGenProp[A](ga: Gen[A])= Prop.forAll(G.uniqueListN[A](ga)(isEqual[A])) { assertUniqueValues }

  private def containerWithUniqueValuesWithNArbProp[A: Arb]= Prop.forAll(Gen.sized(size => G.uniqueListN[A](size, arb[A], size * 2)(isEqual[A]))) { assertUniqueValues }

  private def containerWithUniqueValuesWithNGenProp[A](ga: Gen[A])= Prop.forAll(Gen.sized(size => G.uniqueListN[A](size, ga, size * 2)(isEqual[A]))) { assertUniqueValues }

  property("should always contain unique elements for Ints with Arb") =
    containerWithUniqueValuesArbProp[Int]

  property("should always contain unique elements for Chars with Arb") =
    containerWithUniqueValuesArbProp[Char]

  property("should always contain unique elements for AnyVal with Arb") =
    containerWithUniqueValuesArbProp[AnyVal]

  property("should always contain unique elements for Ints with Gen") =
    containerWithUniqueValuesGenProp(arb[Int])

  property("should always contain unique elements for Chars with Gen") =
    containerWithUniqueValuesGenProp(arb[Char])

  property("should always contain unique elements for AnyVal with Gen") =
    containerWithUniqueValuesGenProp(arb[AnyVal])

  property("should always contain unique elements for Ints given n with Arb") =
    containerWithUniqueValuesWithNArbProp[Int]

  property("should always contain unique elements for Chars given n with Arb") =
    containerWithUniqueValuesWithNArbProp[Char]

  property("should always contain unique elements for AnyVal given n with Arb") =
    containerWithUniqueValuesWithNArbProp[AnyVal]

  property("should always contain unique elements for Ints given n with Gen") =
    containerWithUniqueValuesWithNGenProp(arb[Int])

  property("should always contain unique elements for Chars given n with Gen") =
    containerWithUniqueValuesWithNGenProp(arb[Char])

  property("should always contain unique elements for AnyVal given n with Gen") =
    containerWithUniqueValuesWithNGenProp(arb[AnyVal])
}