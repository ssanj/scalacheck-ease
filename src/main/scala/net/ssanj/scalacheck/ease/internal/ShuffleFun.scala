package net.ssanj.scalacheck.ease.internal

import scala.language.higherKinds
import scala.collection.generic.CanBuildFrom
 import org.scalacheck._
 import org.scalacheck.rng.Seed

trait ShuffleFun {

  // def shuffle[C[X] <: TraversableOnce[X], A](gac: Gen[C[A]])(
  //   implicit bf: CanBuildFrom[C[A], A, C[A]]): Gen[C[A]] =
  //     gac.map(scala.util.Random.shuffle(_))

  def shuffle[C[X] <: TraversableOnce[X], A](values: C[A])(
    implicit bf: CanBuildFrom[C[A], A, C[A]]): C[A] = scala.util.Random.shuffle(values)

  def shuffle2[A](values: List[A]): Seed => Gen[List[A]] = seed => {

      @annotation.tailrec
      def doShuffle2(p: Gen.Parameters, s: Seed, oldList: List[A], newList: List[A]): Gen[List[A]] = {
        if (oldList.isEmpty) Gen.const(newList)
        else {
          Gen.choose(0, oldList.length - 1).apply(p, seed) match {
            case None => Gen.fail[List[A]]
            case Some(index) =>
              val value = oldList(index)
              val updatedOldList = oldList.take(index) ++ oldList.drop(index + 1)
              doShuffle2(p, seed.next, updatedOldList, value +: newList)
          }
        }
      }

      Gen.parameterized(doShuffle2(_, seed, values, List.empty[A]))
    }
}