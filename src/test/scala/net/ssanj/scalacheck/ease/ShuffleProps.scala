package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._
import org.scalacheck.Prop._
import syntax._

object ShuffleProps extends Properties("shuffle") {

  final case class AtLeastTwo(value: Int)
  final case class Original[A](value: List[A])
  final case class Shuffled[A](value: List[List[A]])
  final case class Picked[A](value: List[List[A]])

  //TODO: Limit scope to ShuffleProps
  object AtLeastTwo {
    implicit val atLeastTwoShrink: Shrink[AtLeastTwo]= Shrink {
      case AtLeastTwo(v) if v >= 2 => (2 until v).map(AtLeastTwo(_)).reverse.toStream
      case AtLeastTwo(_) => Stream.Empty
    }

    import scala.language.implicitConversions
    implicit def atLeastTwoPretty(alt: AtLeastTwo): util.Pretty = util.Pretty(_ => alt.value.toString)

    implicit val arbAtLeastTwo: Arb[AtLeastTwo] =
      Arb(
        Gen.sized{ size =>
        Gen.choose(2, Math.max(2L, size).toInt).map(AtLeastTwo(_))
      })
  }

  implicit def arbShuffledAndPicked[A: Arb]: Arb[(Original[A], Shuffled[A], Picked[A])]  = Arb {
      for {
        size                   <- Gen.choose(2, 100)
        seed                   <- Gen.choose(Int.MaxValue / 2, Int.MaxValue)
        seed1                  =  rng.Seed(seed)
        seed2                  =  seed1.next
        seed3                  =  seed2.next
        original               <- G.containerNSeed(size, arb[A])(seed1)
        sampleSize             <- Gen.choose(100, 1000)
        shuffled               <- G.containerNSeed(sampleSize, Gen.delay(Gen.const(G.shuffle(original))))(seed2)
        picked                 <- G.containerNSeed(sampleSize, Gen.pick(size, original).map(_.toList))(seed3)
      } yield (Original(original), Shuffled(shuffled), Picked(picked))
    }

  implicit def arbShuffled[A: Arb]: Arb[(Original[A], Shuffled[A])]  = Arb {
      for {
        size                   <- Gen.choose(2, 100)
        seed                   <- Gen.choose(Int.MaxValue / 2, Int.MaxValue)
        seed1                  =  rng.Seed(seed)
        seed2                  =  seed1.next
        original               <- G.containerNSeed(size, arb[A])(seed1)
        sampleSize             <- Gen.choose(100, 1000)
        shuffled               <- G.containerNSeed(sampleSize, Gen.delay(Gen.const(G.shuffle(original))))(seed2)
      } yield (Original(original), Shuffled(shuffled))
    }

  property("should be less than 90% identical to original on average") =
    Prop.forAll { pair: Tuple2[Original[Int], Shuffled[Int]] =>
        val original   = pair._1.value
        val shuffled   = pair._2.value
        val mapped     = shuffled.groupBy(_.mkString).mapValues(_.length)

        mapped.get(original.mkString).fold(Prop.passed) { value =>
          (value / shuffled.length.toDouble < 0.70) :|
            s"> More than 90% identical to original\n" +
            s"> Original: ${original.mkString}\n" +
            s"> Mapped:\n${mapped.mkString("\n")}"
        }
    }

  property("should have better spread than Gen.pick") =
    Prop.forAll { triple: Tuple3[Original[Int], Shuffled[Int], Picked[Int]] =>
        val original      = triple._1.value
        val shuffled      = triple._2.value
        val picked        = triple._3.value
        val shuffleMapped = shuffled.groupBy(_.mkString).mapValues(_.length)
        val pickedMapped  = picked.groupBy(_.mkString).mapValues(_.length)

        val shuffleKeys   = shuffleMapped.keySet
        val pickedKeys    = pickedMapped.keySet

        (shuffleKeys.size > pickedKeys.size) :| s"> Same spread:\n> Orignal keys: ${original.mkString(",")}\n> Shuffle keys: ${shuffleKeys.mkString(",")}\n> Pick keys: ${pickedMapped.keySet.mkString(",")}"
    }
}