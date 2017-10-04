package net.ssanj.scalacheck.ease

import scala.annotation.tailrec
import org.scalacheck.Gen

object G {

  /**
   * Returns a Some(List[A]) of the required size with elements from a supplied
   * generator of A, or fails with a None if a List of the given size can't
   * be generated.
   *
   * Discards any failures from the supplied generator and only returns
   * valid generation values. If the number of retries is exhausted before the required number of elements is collected,
   * any collected values are discarded and a None is returned.
   *
   * In the example below, the generator either fails or chooses a number between 1 and 5. Given
   * a suitable number of retries, this generator can be used to yield a List of 10 Ints between 1 and 5.
   *
   * {{{
   *   sampleN(10, Gen.oneOf(Gen.fail, Gen.choose(1, 5)), 20)
   * }}}
   *
   * Use it without retries for stable generators that won't fail:
   *
   * {{{
   *   sampleN(20, Gen.alphaStr, 0)
   * }}}
   *
   * @tparam A The type of values generated from the generator.
   * @param n The number of elements required from the supplied generator.
   * @param ga The generator of values of type A.
   * @param r The number of times to retry a fail generator.
   */
  def sampleN[A](n: Int, ga: Gen[A], r: Int): Option[List[A]] = {

    @tailrec
    def doSampleN(retries: Int, results: List[A]): Option[List[A]] = {
        if (results.length >= n) Option(results)
        else if (retries < 0) (None: Option[List[A]]) //less than n elements and no retries left
        else {
          val round = ga.sample
          val newRetries = round.fold(retries - 1)(_ => retries)
          doSampleN(newRetries, round.fold(results)(_ +: results))
        }
    }

    doSampleN(r, List.empty[A])
  }
}