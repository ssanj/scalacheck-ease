package net.ssanj.scalacheck.ease

import scala.annotation.tailrec
import org.scalacheck.Gen

object G {

  /**
   * A sample method that returns a List of the required size *n* as a Some or fails
   * with a None.
   *
   * {{{
   *   sampleN(10, Gen.oneOf(Gen.fail, Gen.choose(1, 5)), 100)
   * }}}
   *
   * @tparam A The type of values generated from the Generator.
   * @param n The number of elements required from the supplied Generator.
   * @param ga The Generator of type A.
   * @param retries The number of types to retry a fail generation.
   * @return A Some List of the requested size *n* or a None
   */
  def sampleN[A](n: Int, ga: Gen[A], retries: Int): Option[List[A]] = {

    @tailrec
    def doSampleN(_n: Int, _ga: Gen[A], _retries: Int, _results: List[A]): Option[List[A]] = {
        if (_results.length == _n) Option(_results)
        else if (_retries <= 0) (None: Option[List[A]]) //less than n elements and no retries left
        else {
          doSampleN(n, _ga, _retries - 1, _ga.sample.fold(_results)(_ +: _results))
        }
    }

    doSampleN(n, ga, retries, List.empty[A])
  }
}