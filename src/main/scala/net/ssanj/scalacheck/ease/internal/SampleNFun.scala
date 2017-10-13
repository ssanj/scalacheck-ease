package net.ssanj.scalacheck.ease.internal

import scala.annotation.tailrec
import org.scalacheck.Gen

trait SampleNFun {
    /**
   * Returns a Some(Vector[A]) of the required size with elements from a supplied
   * generator of A, or fails with a None if a Vector of the given size can't
   * be generated.
   *
   * Discards any failures from the supplied generator and only returns
   * valid generation values. If the number of retries is exhausted before the required number of elements is collected,
   * any collected values are discarded and a None is returned.
   *
   * In the example below, the generator either fails or chooses a number between 1 and 5. Given
   * a suitable number of retries, this generator can be used to yield a Vector of 10 Ints between 1 and 5.
   *
   * @example {{{
   *   sampleN(10, Gen.oneOf(Gen.fail, Gen.choose(1, 5)), 20)
   * }}}
   *
   * To use it without for generators that won't fail, set retries to 0:
   *
   * @example {{{
   *   sampleN(20, Gen.alphaStr, 0)
   * }}}
   *
   * or call sampleN[A](n: Int, ga: Gen[A]): Option[Vector[A]]
   *
   * @tparam A The type of values generated from the generator.
   * @param n The number of elements required from the supplied generator.
   * @param ga The generator of values of type A.
   * @param r The number of times to retry a fail generator.
   */
  def sampleN[A](n: Int, ga: Gen[A], r: Int): Option[Vector[A]] = {

    @tailrec
    def doSampleN(retries: Int, results: Vector[A]): Option[Vector[A]] = {
        if (results.length >= n) Option(results)
        else if (retries < 0) (None: Option[Vector[A]]) //less than n elements and no retries left
        else {
          val round = ga.sample
          val newRetries = round.fold(retries - 1)(_ => retries)
          doSampleN(newRetries, round.fold(results)(_ +: results))
        }
    }

    doSampleN(r, Vector.empty[A])
  }

  /**
   * Returns a Some(Vector[A]) of the required size with elements from a supplied
   * generator of A, or fails with a None if a Vector of the given size can't
   * be generated. Does not handle retrying any errors from the supplied generator.
   * If retries are needed use the other variation of this method.
   *
   * @example {{{
   *   sampleN(20, Gen.alphaStr)
   * }}}
   */
  def sampleN[A](n: Int, ga: Gen[A]): Option[Vector[A]] = sampleN[A](n, ga, 0)
}