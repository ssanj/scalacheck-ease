package net.ssanj.scalacheck.ease

import org.scalacheck._
import org.scalacheck.Prop._
import syntax._

object GSampleNProps extends Properties("G.sampleN") {

  private val faultyGen: Gen[String] = Gen.oneOf(Gen.fail, Gen.alphaStr)

  final case class Int1000(value: Int)

  private implicit val arbInt1000 = Arb(Gen.choose(0, 1000).map(Int1000))

  property("result should be of the requested size with failing Gens and retries") =
    Prop.forAll { m: Int1000 =>
      val n = m.value
      val result = G.sampleN(n, faultyGen, Math.floor(n * 1.1).toInt) //allow some failures
      classify(result.isDefined, "Some", "None") {
        result.fold(passed)(x => (x.length ?= n) && x.forall(_.forall(_.isLetter)))
      }
    }

  property("should handle valid Gens without retries") = {
    Prop.forAll {  m: Int1000 =>
      val n = m.value
      val results = G.sampleN(n, arb[Int], 0)
      results.fold(falsified)(_.length ?= n)
    }
  }

  property("result should be of requested size with valid Gens") = {
    Prop.forAll {  m: Int1000 =>
      val n = m.value
      val results = G.sampleN(n, arb[AnyVal])
      results.fold(falsified)(_.length ?= n)
    }
  }

  property("should return None when given a Gen that always fails") = {
      Prop.forAll {  m: Int1000 =>
        val n = m.value
        val results = G.sampleN(n, Gen.fail[Boolean])
        results.fold(passed)(_ => falsified)
      }
  }
}