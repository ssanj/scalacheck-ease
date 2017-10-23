package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._
import syntax._

object UniqueContainerProps extends Properties("UniqueContainerN") {

  private def assertUnqiueValues[A](values: List[A]): Prop = values.toSet.size == values.length

  private def assertLength[A](values: List[A], size: Int): Prop = values.length == size

  private def containerWithUniqueValuesProp[A: Arb]= Prop.forAll(G.uniqueListNSized[A](_ == _)) { case (ul, _) => assertUnqiueValues(ul) }

  private def containerWithSizeProp[A: Arb] = Prop.forAll(G.uniqueListNSized[A](_ == _)) { (assertLength _).tupled }

  property("should always contain unique elements for Ints") =
    containerWithUniqueValuesProp[Int]

  property("should always contain unique elements for Chars") =
    containerWithUniqueValuesProp[Char]

  property("should always contain unique elements for AnyVal") =
    containerWithUniqueValuesProp[AnyVal]

  property("should always return Int containers of the required size") =
    containerWithSizeProp[Int]

  property("should always return Char containers of the required size") =
    containerWithSizeProp[Char]

  property("should always return AnyVal containers of the required size") =
    containerWithSizeProp[AnyVal]
}