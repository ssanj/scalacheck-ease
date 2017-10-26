package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._
import UniqueListNSized._

object UniqueListNRSizedProps {

  def properties: Properties =
    new Properties("uniqueListNRSized") {
       property("should always return Int containers of the required size with Arb") =
        containerWithResizeArbProp(G.uniqueListNRSized[Int])

      property("should always return Char containers of the required size with Arb") =
        containerWithResizeArbProp(G.uniqueListNRSized[Char])

      property("should always return AnyVal containers of the required size with Arb") =
        containerWithResizeArbProp(G.uniqueListNRSized[AnyVal])

      property("should always return Int containers of the required size with Gen") =
        containerWithResizeGenProp(G.uniqueListNRSized[Int])

      property("should always return Char containers of the required size with Gen") =
        containerWithResizeGenProp(G.uniqueListNRSized[Char])

      property("should always return AnyVal containers of the required size with Gen") =
        containerWithResizeGenProp(G.uniqueListNRSized[AnyVal])
    }

}
