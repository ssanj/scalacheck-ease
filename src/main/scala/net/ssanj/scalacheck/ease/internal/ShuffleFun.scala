package net.ssanj.scalacheck.ease.internal

import scala.language.higherKinds
import scala.collection.generic.CanBuildFrom
//  import org.scalacheck.Gen

trait ShuffleFun {

  // def shuffle[C[X] <: TraversableOnce[X], A](gac: Gen[C[A]])(
  //   implicit bf: CanBuildFrom[C[A], A, C[A]]): Gen[C[A]] =
  //     gac.map(scala.util.Random.shuffle(_))

  def shuffle[C[X] <: TraversableOnce[X], A](values: C[A])(
    implicit bf: CanBuildFrom[C[A], A, C[A]]): C[A] = scala.util.Random.shuffle(values)
}