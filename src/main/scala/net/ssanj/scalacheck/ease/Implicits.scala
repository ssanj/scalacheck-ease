package net.ssanj.scalacheck.ease

import org.scalacheck.Gen
import org.scalacheck.rng.Seed
import syntax._

object Implicits {
  implicit def arbSeed: Arb[Seed] = Arb { Gen.choose(Int.MinValue, Int.MaxValue).map(Seed(_)) }
}