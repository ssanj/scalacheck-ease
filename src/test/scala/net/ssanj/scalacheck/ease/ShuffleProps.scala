package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._
import org.scalacheck.rng.Seed
import org.scalacheck.Prop._
import syntax._
import Implicits._

object ShuffleProps extends Properties("shuffle") {

  final case class Original[A](value: List[A])
  final case class Shuffled[A](value: List[List[A]])
  final case class Picked[A](value: List[List[A]])

  private implicit def arbShuffledAndPicked[A: Arb]: Arb[(Original[A], Shuffled[A], Picked[A])]  = Arb {
      for {
        size       <- Gen.choose(10, 200)
        seed1      <- arb[Seed]
        seed2      <- arb[Seed]
        seed3      <- arb[Seed]
        original   <- G.containerNSeed(size, discardSeedArb[A])(seed1)
        sampleSize <- Gen.choose(100, 1000)
        shuffled   <- G.containerNSeed(sampleSize, G.shuffle2(original))(seed2)
        picked     <- G.containerNSeed(sampleSize, discardSeedGen[List[A]](Gen.pick(size, original).map(_.toList)))(seed3)
      } yield (Original(original), Shuffled(shuffled), Picked(picked))
    }

  private implicit def arbShuffled[A: Arb]: Arb[(Original[A], Shuffled[A])]  = Arb {
      for {
        size       <- Gen.choose(10, 200)
        seed1      <- arb[Seed]
        seed2      <- arb[Seed]
        original   <- G.containerNSeed(size, discardSeedArb[A])(seed1)
        sampleSize <- Gen.choose(100, 1000)
        shuffled   <- G.containerNSeed(sampleSize, G.shuffle2(original))(seed2)
      } yield (Original(original), Shuffled(shuffled))
    }

  private implicit def arbOriginal[A: Arb]: Arb[Original[A]] = Arb {
    for {
      size     <- Gen.choose(10, 200)
      seed1    <- arb[Seed]
      original <- G.containerNSeed(size, discardSeedArb[A])(seed1)
    } yield (Original(original))
  }

  private def groupByValues[A](list: List[List[A]]): Map[String, Int] =
    list.groupBy(_.mkString("#")).mapValues(_.length)

  property("should be less than 70% identical to original on average") =
    Prop.forAll { pair: Tuple2[Original[Int], Shuffled[Int]] =>
        val original   = pair._1.value
        val shuffled   = pair._2.value
        val mapped     = groupByValues(shuffled)

        mapped.get(original.mkString).fold(Prop.passed) { value =>
          (value / shuffled.length.toDouble < 0.70) :|
            s"> More than 70% identical to original\n" +
            s"> Original: ${original.mkString}\n" +
            s"> Mapped:\n${mapped.mkString("\n")}"
        }
    }

  property("should have better spread than Gen.pick") =
    Prop.forAll { triple: Tuple3[Original[Int], Shuffled[Int], Picked[Int]] =>
        val original      = triple._1.value
        val shuffled      = triple._2.value
        val picked        = triple._3.value
        val shuffleMapped = groupByValues(shuffled)
        val pickedMapped  = groupByValues(picked)

        val shuffleKeys   = shuffleMapped.keySet
        val pickedKeys    = pickedMapped.keySet

        (shuffleKeys.size > pickedKeys.size) :|
          s"> Same spread:\n" +
          s"> Original keys: ${original.mkString(",")}\n" +
          s"> Shuffle keys: ${shuffleKeys.mkString(",")}\n" +
          s"> Pick keys: ${pickedMapped.keySet.mkString(",")}"
    }

  private def deterministicBySeedProperty[A: Arb]: Prop =
    Prop.forAll { original: Original[A] =>
      val value  = original.value
      val seed1  = Seed.random()
      val seed2  = Seed.random()
      val params = Gen.Parameters.default
      G.shuffle2(value)(seed1)(params, seed2) ?= G.shuffle2(value)(seed1)(params, seed2)
    }

  property("should be deterministic by Seed for Int") = deterministicBySeedProperty[Int]

  property("should be deterministic by Seed for Char") = deterministicBySeedProperty[Char]

  property("should be deterministic by Seed for AnyVal") = deterministicBySeedProperty[AnyVal]

}