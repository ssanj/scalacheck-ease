package net.ssanj.scalacheck.ease

import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.rng.Seed

package object syntax {

  //an easy way to get a Gen[T] from an Arbitrary[T]
  def arb[T: Arbitrary] = Arbitrary.arbitrary[T]

  //shorthand for creating Arbitrary instances
  val Arb = Arbitrary

  type Arb[A] = Arbitrary[A]

  type SGen[A] = Seed => Gen[A]

  type SArb[A] = Seed => Arb[A]

  def discardSeedGen[A](ga: Gen[A]): Seed => Gen[A] = _ => ga

  def discardSeedArb[A: Arb]: Seed => Gen[A] = _ => arb[A]

}
