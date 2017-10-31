package net.ssanj.scalacheck.ease.internal

import org.scalacheck._

trait ContainerN {

  def containerNSeed[A](n: Int, ga: Gen[A])(seed: rng.Seed): Gen[List[A]] = {

    @annotation.tailrec
    def doContainerN(p: Gen.Parameters, s: rng.Seed, results: List[A]): Gen[List[A]] = {
      if (results.length >= n) Gen.const(results)
      else {
        ga.apply(p, s) match {
          case None => Gen.fail[List[A]]
          case Some(value) => doContainerN(p, s.next, value +: results)
        }
      }
    }

    Gen.parameterized(doContainerN(_, seed, List.empty[A]))
  }
}