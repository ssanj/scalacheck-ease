package net.ssanj.scalacheck.ease

import org.scalacheck.Arbitrary

package object syntax {

  //an easy way to get a Gen[T] from an Arbitrary[T]
  def arb[T: Arbitrary] = Arbitrary.arbitrary[T]

  //shorthand for creating Arbitrary instances
  val Arb = Arbitrary
}
