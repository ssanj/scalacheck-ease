package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._
import UniqueListNSized._

object UniqueListNSizedProps  {

  def properties: Properties =
    new Properties("uniqueListNSized") {
       property("should always return Int containers of the required size with Arb") =
        containerWithSizeArbProp(G.uniqueListNSized[Int])

      property("should always return Char containers of the required size with Arb") =
        containerWithSizeArbProp(G.uniqueListNSized[Char])

      property("should always return AnyVal containers of the required size with Arb") =
        containerWithSizeArbProp(G.uniqueListNSized[AnyVal])

      property("should always return Int containers of the required size with Gen") =
        containerWithSizeGenProp(G.uniqueListNSized[Int])

      property("should always return Char containers of the required size with Gen") =
        containerWithSizeGenProp(G.uniqueListNSized[Char])

      property("should always return AnyVal containers of the required size with Gen") =
        containerWithSizeGenProp(G.uniqueListNSized[AnyVal])
    }
}
