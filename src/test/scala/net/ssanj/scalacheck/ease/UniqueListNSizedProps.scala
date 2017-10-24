package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._
import syntax._

object UniqueListNSizedProps extends Properties("uniqueListNSized") {

  private def assertLength[A]: ((List[A], Int)) => Prop = { case (values, size) => values.length == size }

  private def isEqual[A]: (A , A) => Boolean = _ == _

  private def containerWithSizeArbProp[A: Arb] = Prop.forAll(G.uniqueListNSized[A](isEqual[A])) { assertLength }

  private def containerWithSizeGenProp[A](ga: Gen[A]) = Prop.forAll(G.uniqueListNSized[A](ga)(isEqual[A])) { assertLength }

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
