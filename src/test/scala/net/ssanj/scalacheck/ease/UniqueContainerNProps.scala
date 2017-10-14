package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._
import syntax._

object UniqueContainerProps extends Properties("UniqueContainerN") {

  private def genUniqueContainer[A: Arb](isEqual: (A, A) => Boolean): Gen[List[A]] = for {
    size   <- Gen.choose(0, 100)
    values <- G.uniqueContainerN(size, arb[A], size * 10)(isEqual)
  } yield values

  property("should always contain unique elements") =
    Prop.forAll(genUniqueContainer[Int]((v1, v2) => v1 == v2)) { values: List[Int] =>
      values.toSet.size == values.length
    }
}