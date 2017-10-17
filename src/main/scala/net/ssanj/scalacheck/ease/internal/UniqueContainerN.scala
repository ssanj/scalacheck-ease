package net.ssanj.scalacheck.ease.internal

import org.scalacheck._

trait UniqueContainerN {

  private def found[A](l: List[A], value: A, isEqual: (A, A) => Boolean): Boolean = {
    l.exists(isEqual(_, value))
  }

  def uniqueContainerN[A](n: Int, ga: Gen[A], r: Int)(isEqual: (A, A) => Boolean, seed: rng.Seed): Gen[List[A]] = {

    @annotation.tailrec
    def doUniqueContainerN(p: Gen.Parameters, s: rng.Seed, retries: Int, results: List[A]): Gen[List[A]] = {
      if (results.length >= n) Gen.const(results)
      else {
        ga.apply(p, s) match {
          case None => Gen.fail[List[A]]
          case Some(value) =>
            if (found(results, value, isEqual)) {
              if (retries <= 0) Gen.fail[List[A]]
              else doUniqueContainerN(p, s.next, retries - 1, results)
            } else doUniqueContainerN(p, s.next, retries, value +: results)
        }
      }
    }

    Gen.parameterized(doUniqueContainerN(_, seed, r, List.empty[A]))
  }

}