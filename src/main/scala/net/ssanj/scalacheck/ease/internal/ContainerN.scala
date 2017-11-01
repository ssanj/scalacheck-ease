package net.ssanj.scalacheck.ease.internal

import org.scalacheck._
import org.scalacheck.rng.Seed
import net.ssanj.scalacheck.ease.syntax._

trait ContainerN {

  def containerNSeed[A](n: Int, f: SGen[A]): SGen[List[A]] = seed => {

    @annotation.tailrec
    def doContainerN(p: Gen.Parameters, s: Seed, results: List[A]): Gen[List[A]] = {
      if (results.length >= n) Gen.const(results)
      else {
        f(s).apply(p, s.next) match {
          case None => Gen.fail[List[A]]
          case Some(value) => doContainerN(p, s.next, value +: results)
        }
      }
    }

    Gen.parameterized(doContainerN(_, seed, List.empty[A]))
  }
}