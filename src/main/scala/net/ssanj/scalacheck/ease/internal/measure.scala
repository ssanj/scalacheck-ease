package net.ssanj.scalacheck.ease
package internal

object Measure {

  sealed trait Num[A] extends Product with Serializable {
    val value: A
    val label: String = this.toString.takeWhile(_ != '(') //hacky
  }

  final case class NumKey(weight: Int, label: String)

  object NumKey {
    implicit val numKeyOrdering: Ordering[NumKey] = new Ordering[NumKey] {
      def compare(nk1: NumKey, nk2: NumKey): Int = nk1.weight.compare(nk2.weight)
    }
  }

  object Num {
    def key[A](weight: Int, num: Num[A]): NumKey = NumKey(weight, num.label)
  }

  object IntSpread {

    def toNums(xs: Vector[Int]): Vector[Num[Int]] = xs map {
      case Int.MinValue => IntMin
      case x if x >= Int.MinValue + 1 && x <= -1000000001 => MinToNegBillions(x)
      case x if x >= -1000000000 && x <= -100000001 => NegBillions(x)
      case x if x >= -100000000 && x <= -10000001 => NegHundredMillions(x)
      case x if x >= -10000000 && x <= -1000001 => NegTenMillions(x)
      case x if x >= -1000000 && x <= -100001 => NegMillions(x)
      case x if x >= -100000 && x <= -10001 => NegHundredThousands(x)
      case x if x >= -10000 && x <= -1001 => NegTenThousands(x)
      case x if x >= -1000 && x <= -101 => NegThousands(x)
      case x if x >= -100 && x <= -2 => NegHundreds(x)
      case -1 => NegOne
      case 0 => Zero
      case 1 => One
      case x if x >= 2 && x <= 100 => Hundreds(x)
      case x if x >= 101 && x <= 1000 => Thousands(x)
      case x if x >= 1001 && x <= 10000 => TenThousands(x)
      case x if x >= 10001 && x <= 100000 => HundredThousands(x)
      case x if x >= 100001 && x <= 1000000 => Millions(x)
      case x if x >= 1000001 && x <= 10000000 => TenMillions(x)
      case x if x >= 10000001 && x <= 100000000 => HundredMillions(x)
      case x if x >= 100000001 && x <= 1000000000 => Billions(x)
      case x if x >= 1000000001 && x <= Int.MaxValue - 1 => BillionsToMax(x)
      case Int.MaxValue => IntMax
      case x => Other(x)
    }

    def getWeight(num: Num[Int]): Int = num match {
      case IntMin                 => 1
      case MinToNegBillions(_)    => 2
      case NegBillions(_)         => 3
      case NegHundredMillions(_)  => 4
      case NegTenMillions(_)      => 5
      case NegMillions(_)         => 6
      case NegHundredThousands(_) => 7
      case NegTenThousands(_)     => 8
      case NegThousands(_)        => 9
      case NegHundreds(_)         => 10
      case NegOne                 => 11
      case Zero                   => 12
      case One                    => 13
      case Hundreds(_)            => 14
      case Thousands(_)           => 15
      case TenThousands(_)        => 16
      case HundredThousands(_)    => 17
      case Millions(_)            => 18
      case TenMillions(_)         => 19
      case HundredMillions(_)     => 20
      case Billions(_)            => 21
      case BillionsToMax(_)       => 22
      case IntMax                 => 23
      case Other(_)               => 24
    }

    def toMap(xs: Vector[Int]): Map[NumKey, Vector[Num[Int]]] =
      toNums(xs).groupBy(num => Num.key(getWeight(num), num))

    //TODO: We shouldn't have to repeat this list of names. We should be able to derive it.
    val defaultLegendKeys: Set[String] =
      Set(
        "IntMin",
        "MinToNegBillions",
        "NegBillions",
        "NegHundredMillions",
        "NegTenMillions",
        "NegMillions",
        "NegHundredThousands",
        "NegTenThousands",
        "NegThousands",
        "NegHundreds",
        "NegOne",
        "Zero",
        "One",
        "Hundreds",
        "Thousands",
        "TenThousands",
        "HundredThousands",
        "Millions",
        "TenMillions",
        "HundredMillions",
        "Billions",
        "BillionsToMax",
        "IntMax",
        "Other"
      )

    def format(ledger: Map[NumKey, Vector[Num[Int]]], sep: String): String = {

      val maxKeyLength = defaultLegendKeys.maxBy(_.length).length

      import scala.collection.immutable.TreeMap
      val recoredValues =
        (TreeMap[NumKey, Vector[Num[Int]]]() ++ ledger).toVector.map {
        case (NumKey(_, label), xs) => s"${label.padTo(maxKeyLength, " ").mkString} -> ${xs.length}"
      }

      val missingKeys = (defaultLegendKeys -- ledger.keySet.map(_.label)).mkString(sep)
      //TODO: Remove other if it's the only missing key
      recoredValues.mkString(sep) + "\n" + s"Missing:\n${missingKeys}"
    }

    import org.scalacheck.Gen
    //TODO: This is not safe. Find a way to handle a Gen that fails - but still keep going
    def stats(num: Int, gen: Gen[Int]): Unit = println(format(toMap(Gen.infiniteStream(gen).sample.get.take(num).toVector), "\n"))

    final case object IntMin extends Num[Int] {
      val value = Int.MinValue
    }

    final case object NegOne extends Num[Int] {
     val value = -1
    }

    final case object Zero extends Num[Int] {
      val value = 0
    }

    final case object One extends Num[Int] {
      val value = 1
    }

    final case class NegHundreds(val value: Int) extends Num[Int]
    final case class Hundreds(val value: Int) extends Num[Int]
    final case class NegThousands(val value: Int) extends Num[Int]
    final case class Thousands(val value: Int) extends Num[Int]
    final case class NegTenThousands(val value: Int) extends Num[Int]
    final case class TenThousands(val value: Int) extends Num[Int]
    final case class NegHundredThousands(val value: Int) extends Num[Int]
    final case class HundredThousands(val value: Int) extends Num[Int]
    final case class NegMillions(val value: Int) extends Num[Int]
    final case class Millions(val value: Int) extends Num[Int]
    final case class NegTenMillions(val value: Int) extends Num[Int]
    final case class TenMillions(val value: Int) extends Num[Int]
    final case class NegHundredMillions(val value: Int) extends Num[Int]
    final case class HundredMillions(val value: Int) extends Num[Int]
    final case class NegBillions(val value: Int) extends Num[Int]
    final case class Billions(val value: Int) extends Num[Int]
    final case class BillionsToMax(val value: Int) extends Num[Int]
    final case class MinToNegBillions(val value: Int) extends Num[Int]
    final case class Other(val value: Int) extends Num[Int]

    final case object IntMax extends Num[Int] {
      val value = Int.MaxValue
    }
  }

}