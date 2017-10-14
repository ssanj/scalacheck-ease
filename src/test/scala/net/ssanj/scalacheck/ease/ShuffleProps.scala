package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._
import org.scalacheck.Prop._

object ShuffleProps extends Properties("shuffle") {

  final case class AtLeastTwo(value: Int)

  object AtLeastTwo {
    implicit val atLeastTwoShrink: Shrink[AtLeastTwo]= Shrink {
      case AtLeastTwo(v) if v >= 2 => (2 until v).map(AtLeastTwo(_)).reverse.toStream
      case AtLeastTwo(_) => Stream.Empty
    }

    import scala.language.implicitConversions
    implicit def atLeastTwoPretty(alt: AtLeastTwo): util.Pretty = util.Pretty(_ => alt.value.toString)
  }

  //TODO: If we had some way to remove duplicates we could generisize this
  //def genAtLeastTwo[A: Arbitrary]: Gen[List[A]]
  private val genAtLeastTwo: Gen[AtLeastTwo] = Gen.sized{ size =>
    Gen.choose(2, Math.min(Int.MaxValue, Math.max(2, size))).map(AtLeastTwo(_))
  }

  property("should be less than 90% identical to original on average") =
    Prop.forAll(genAtLeastTwo) {
      case AtLeastTwo(size) =>
        val original   = (1 to size).toList
        val iterations = Math.min(Int.MaxValue, size * 100)
        val shuffled   = List.fill(iterations)(G.shuffle(original))
        val mapped     = shuffled.groupBy(_.mkString).mapValues(_.length)

        mapped.get(original.mkString).fold(Prop.passed) { value =>
          (value / iterations.toDouble < 0.70) :|
            s"> More than 90% identical to original\n" +
            s"> Original: ${original.mkString}\n" +
            s"> Mapped:\n${mapped.mkString("\n")}"
        }
    }

  //TODO: Make this nicer
  //1. Pass in original list or
  //2. Have a way to pass in shuffled and picked lists from Gen
  property("should have better spread than Gen.pick") =
    Prop.forAll(genAtLeastTwo) {
      case AtLeastTwo(size) =>
        val original   = (1 to size).toList
        val iterations = Math.min(Int.MaxValue, size * 100)
        val shuffled   = List.fill(iterations)(G.shuffle(original))
        val picked     = List.fill(iterations)(Gen.pick(size, original).sample.toList.flatten)
        val shuffleMapped = shuffled.groupBy(_.mkString).mapValues(_.length)
        val pickedMapped  = picked.groupBy(_.mkString).mapValues(_.length)

        val shuffleKeys = shuffleMapped.keySet
        val pickedKeys = pickedMapped.keySet

        (shuffleKeys.size > pickedKeys.size) :| s"> Same spread:\n> Shuffle keys: ${shuffleKeys.mkString(",")}\n> Pick keys: ${pickedMapped.keySet.mkString(",")}"
    }
}