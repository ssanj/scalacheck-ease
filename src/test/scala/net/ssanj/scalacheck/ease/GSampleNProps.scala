package net.ssanj.scalacheck.ease

import org.scalacheck._
import org.scalacheck.Prop._
import syntax._

object GSampleNProps extends Properties("G.sampleN") {

  private val faultyGen: Gen[String] = Gen.oneOf(Gen.fail, Gen.alphaStr)

  final case class Int1000(value: Int)

  private implicit val arbInt1000 = Arb(Gen.choose(0, 1000).map(Int1000))

  property("result should be of requested size") =
    Prop.forAll { m: Int1000 =>
      val n = m.value
      val result = G.sampleN(n, faultyGen, Math.floor(n * 1.1).toInt) //allow some failures
      classify(result.isDefined, "Some", "None") {
        result.fold(passed)(x => (x.length ?= n) && x.forall(_.forall(_.isLetter)))
      }
    }

  property("should handle non-failing Gens without retries") = {
    Prop.forAll {  m: Int1000 =>
      val n = m.value
      val results = G.sampleN(n, arb[Int], 0)
      results.fold(falsified)(_.length ?= n)
    }
  }
}