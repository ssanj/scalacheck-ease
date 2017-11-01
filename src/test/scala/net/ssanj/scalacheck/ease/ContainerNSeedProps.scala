package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck.Prop._
import org.scalacheck._
import org.scalacheck.rng.Seed
import syntax._
import Implicits._

object ContainerNSeedProps extends Properties("ContainerNSeed") {

  private def sizedContainer[A: Arb]: Gen[(Int, List[A])] = Gen.sized { size =>
    for {
      seed   <- arb[Seed]
      values <- G.containerNSeed(size, discardSeedArb[A])(seed)
    } yield (size, values)
  }

  private def lengthProp[A: Arb]: Prop =
    Prop.forAllNoShrink(sizedContainer[A]) {
      case (size: Int, container: List[A]) => container.size ?= size
    }

  property("should always be of the requested length for Ints")   = lengthProp[Int]

  property("should always be of the requested length for Chars")  = lengthProp[Char]

  property("should always be of the requested length for AnyVal") = lengthProp[AnyVal]

}