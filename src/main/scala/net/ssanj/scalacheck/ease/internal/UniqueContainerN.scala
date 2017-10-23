package net.ssanj.scalacheck.ease.internal

import org.scalacheck._
import net.ssanj.scalacheck.ease.syntax._

trait UniqueContainerN {

  private def found[A](l: List[A], value: A, isEqual: (A, A) => Boolean): Boolean = {
    l.exists(isEqual(_, value))
  }

  def uniqueListNSized[A: Arb](isEqual: (A, A) => Boolean): Gen[(List[A], Int)] = Gen.sized(size =>
    uniqueListN(size, arb[A], size * 3)(isEqual).map((_, size))
  )

  def uniqueListNRSized[A: Arb](r: Int)(isEqual: (A, A) => Boolean): Gen[(List[A], Int)] = Gen.sized(size =>
    uniqueListN(size, arb[A], r)(isEqual).map((_, size))
  )

  def uniqueListN[A: Arb](isEqual: (A, A) => Boolean): Gen[List[A]] =
    uniqueListNSized[A](isEqual).map(_._1)

  def uniqueListN[A](n: Int, ga: Gen[A], r: Int)(isEqual: (A, A) => Boolean) =
    uniqueListNSeed[A](n, ga, r)(isEqual, rng.Seed.random)

  def uniqueListNSeed[A](n: Int, ga: Gen[A], r: Int)(isEqual: (A, A) => Boolean, seed: rng.Seed): Gen[List[A]] = {

    @annotation.tailrec
    def doUniqueListN(p: Gen.Parameters, s: rng.Seed, retries: Int, results: List[A]): Gen[List[A]] = {
      if (results.length >= n) Gen.const(results)
      else {
        ga.apply(p, s) match {
          case None => Gen.fail[List[A]]
          case Some(value) =>
            if (found(results, value, isEqual)) {
              if (retries <= 0) Gen.fail[List[A]]
              else doUniqueListN(p, s.next, retries - 1, results)
            } else doUniqueListN(p, s.next, retries, value +: results)
        }
      }
    }

    Gen.parameterized(doUniqueListN(_, seed, r, List.empty[A]))
  }

}