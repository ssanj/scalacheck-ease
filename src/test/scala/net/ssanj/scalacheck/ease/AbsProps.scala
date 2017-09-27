package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._

object AbsProps extends Properties("Math.abs") {

  private val genIntRange: Gen[Int] = Gen.choose(Int.MinValue + 1, Int.MaxValue)

  property("should always be positive") =
    Prop.forAll(genIntRange) { n: Int =>
        Math.abs(n) >= 0
    }
}