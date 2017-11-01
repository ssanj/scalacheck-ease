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

  implicit def arbShuffledAndPicked[A: Arb]: Arb[(Original[A], Shuffled[A], Picked[A])]  = Arb {
      for {
        size                   <- Gen.choose(2, 100)
        seed1                  <- arb[Seed]
        seed2                  <- arb[Seed]
        seed3                  <- arb[Seed]
        original               <- G.containerNSeed(size, discardSeedArb[A])(seed1)
        sampleSize             <- Gen.choose(100, 1000)
        shuffled               <- G.containerNSeed(sampleSize, G.shuffle2(original))(seed2)
        picked                 <- G.containerNSeed(sampleSize, discardSeedGen[List[A]](Gen.pick(size, original).map(_.toList)))(seed3)
      } yield (Original(original), Shuffled(shuffled), Picked(picked))
    }

  implicit def arbShuffled[A: Arb]: Arb[(Original[A], Shuffled[A])]  = Arb {
      for {
        size                   <- Gen.choose(2, 100)
        seed1                  <- arb[Seed]
        seed2                  <- arb[Seed]
        original               <- G.containerNSeed(size, discardSeedArb[A])(seed1)
        sampleSize             <- Gen.choose(100, 1000)
        shuffled               <- G.containerNSeed(sampleSize, G.shuffle2(original))(seed2)
      } yield (Original(original), Shuffled(shuffled))
    }

  property("should be less than 70% identical to original on average") =
    Prop.forAll { pair: Tuple2[Original[Int], Shuffled[Int]] =>
        val original   = pair._1.value
        val shuffled   = pair._2.value
        val mapped     = shuffled.groupBy(_.mkString("#")).mapValues(_.length)

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
        val shuffleMapped = shuffled.groupBy(_.mkString("#")).mapValues(_.length)
        val pickedMapped  = picked.groupBy(_.mkString("#")).mapValues(_.length)

        val shuffleKeys   = shuffleMapped.keySet
        val pickedKeys    = pickedMapped.keySet

        (shuffleKeys.size > pickedKeys.size) :| s"> Same spread:\n> Original keys: ${original.mkString(",")}\n> Shuffle keys: ${shuffleKeys.mkString(",")}\n> Pick keys: ${pickedMapped.keySet.mkString(",")}"
    }
}