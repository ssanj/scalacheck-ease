package net.ssanj.scalacheck.ease

import org.scalacheck.{Arbitrary, Gen}

final case class IntSpread(value: Int)

object IntSpread {

  private val gen100Neg        = Gen.choose(-100, -2)
  private val gen1000Neg       = Gen.choose(-1000, -101)
  private val gen10000Neg      = Gen.choose(-10000, -1001)
  private val gen100000Neg     = Gen.choose(-100000, -10001)
  private val gen1000000Neg    = Gen.choose(-1000000, -100001)
  private val gen10000000Neg   = Gen.choose(-10000000, -1000001)
  private val gen100000000Neg  = Gen.choose(-100000000, -10000001)
  private val gen1000000000Neg = Gen.choose(-1000000000, -100000001)
  private val genMin           = Gen.choose(Int.MinValue + 1, -1000000001)

  private val gen100           = Gen.choose(2, 100)
  private val gen1000          = Gen.choose(101, 1000)
  private val gen10000         = Gen.choose(1001, 10000)
  private val gen100000        = Gen.choose(10001, 100000)
  private val gen1000000       = Gen.choose(100001, 1000000)
  private val gen10000000      = Gen.choose(1000001, 10000000)
  private val gen100000000     = Gen.choose(10000001, 100000000)
  private val gen1000000000    = Gen.choose(100000001, 1000000000)
  private val genMax           = Gen.choose(1000000001, Int.MaxValue - 1)

  implicit val arbIntSpread: Arbitrary[IntSpread] = Arbitrary {
    Gen.frequency(
      (2, Gen.const(Int.MaxValue)),
      (2, Gen.const(Int.MinValue)),
      (2, Gen.const(0)),
      (2, Gen.const(-1)),
      (2, Gen.const(1)),
      (3, gen100Neg),
      (3, gen100),
      (3, gen1000Neg),
      (3, gen1000),
      (3, gen10000Neg),
      (3, gen10000),
      (3, gen100000Neg),
      (2, gen100000),
      (2, gen1000000Neg),
      (2, gen1000000),
      (2, gen10000000Neg),
      (2, gen10000000),
      (2, gen100000000Neg),
      (2, gen100000000),
      (2, gen1000000000Neg),
      (2, gen1000000000),
      (2, genMin),
      (2, genMax)
    ).map(IntSpread(_))
  }
}