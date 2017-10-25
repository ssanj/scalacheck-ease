package net.ssanj.scalacheck.ease

import org.scalacheck._
import syntax._

object UniqueListNSized {

  private def assertLength[A]: ((List[A], Int)) => Prop = { case (values, size) => values.length == size }

  private def isEqual[A]: Predicate[A] = _ == _

  private def resize: Resize = identity

  def containerWithResizeArbProp[A: Arb](f: Resize => Predicate[A] => Gen[(List[A], Int)]): Prop =
    Prop.forAll(f(resize)(isEqual[A])) { assertLength }

  def containerWithSizeArbProp[A: Arb](f: Predicate[A] => Gen[(List[A], Int)]): Prop =
    Prop.forAll(f(isEqual[A])) { assertLength }

  def containerWithResizeGenProp[A: Arb](f: (Gen[A], Resize) => Predicate[A] => Gen[(List[A], Int)]): Prop =
    Prop.forAll(f(arb[A], resize)(isEqual[A])) { assertLength }

  def containerWithSizeGenProp[A: Arb](f: Gen[A] => Predicate[A] => Gen[(List[A], Int)]): Prop =
    Prop.forAll(f(arb[A])(isEqual[A])) { assertLength }
}