package net.ssanj.scalacheck.ease.internal

import org.scalacheck._

trait UniqueContainerN {

  private def found[A](l: List[A], value: A, isEqual: (A, A) => Boolean): Boolean = {
    l.foldLeft(false)( (a, v) => a || isEqual(v, value))
  }

  def uniqueContainerN[A](n: Int, ga: Gen[A], r: Int)(isEqual: (A, A) => Boolean): Gen[List[A]] = {

    def doUniqueContainerN(retries: Int, results: List[A]): Gen[List[A]] = {
      if (results.length >= n) Gen.const(results)
      else {
        for {
          value   <- ga
          isFound = found(results, value, isEqual)
          updated <-  if (isFound) {
                        if (retries <= 0) Gen.fail[List[A]]
                        else doUniqueContainerN(retries - 1, results)
                      } else doUniqueContainerN(retries, value +: results)
        } yield updated
      }
    }

    doUniqueContainerN(r, List.empty[A])
  }

}