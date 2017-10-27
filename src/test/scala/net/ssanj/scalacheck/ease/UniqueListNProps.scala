package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._
import UniqueListNSized._

object UniqueListNProps  {

  def properties: Properties = {
    new Properties("uniqueListN") {
      property("should always contain unique elements for Ints with Arb") =
        containerWithUniqueValuesArbProp(G.uniqueListN[Int])

      property("should always contain unique elements for Chars with Arb") =
        containerWithUniqueValuesArbProp(G.uniqueListN[Char])

      property("should always contain unique elements for AnyVal with Arb") =
        containerWithUniqueValuesArbProp(G.uniqueListN[AnyVal])

      property("should always contain unique elements for Ints with Gen") =
        containerWithUniqueValuesGenProp(G.uniqueListN[Int])

      property("should always contain unique elements for Chars with Gen") =
        containerWithUniqueValuesGenProp(G.uniqueListN[Char])

      property("should always contain unique elements for AnyVal with Gen") =
        containerWithUniqueValuesGenProp(G.uniqueListN[AnyVal])

      property("should always contain unique elements for Ints given n with Arb") =
        containerWithUniqueValuesWithNArbProp(G.uniqueListN[Int])

      property("should always contain unique elements for Chars given n with Arb") =
        containerWithUniqueValuesWithNArbProp(G.uniqueListN[Char])

      property("should always contain unique elements for AnyVal given n with Arb") =
        containerWithUniqueValuesWithNArbProp(G.uniqueListN[AnyVal])

      property("should always contain unique elements for Ints given n with Gen") =
        containerWithUniqueValuesWithNGenProp(G.uniqueListN[Int])

      property("should always contain unique elements for Chars given n with Gen") =
        containerWithUniqueValuesWithNGenProp(G.uniqueListN[Char])

      property("should always contain unique elements for AnyVal given n with Gen") =
        containerWithUniqueValuesWithNGenProp(G.uniqueListN[AnyVal])

      property("should fail if the supplied Generator fails") =
        containerWithUniqueValuesWithNGenFailingProp(G.uniqueListN[AnyVal])
    }
  }
}