package net.ssanj.scalacheck

package object ease {

  type Predicate[A] = (A, A) => Boolean

  type Resize = Int => Int

}