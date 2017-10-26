package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._
import UniqueListNSized._

object UniqueListNSeedProps {

  def properties: Properties = {
    new Properties("uniqueListNSeed") {
      property("should always return Int containers of the required size with Arb") =
          containerWithSizedSeedArbProp[Int](G.uniqueListNSeed[Int])

      property("should always return Char containers of the required size with Arb") =
        containerWithSizedSeedArbProp[Char](G.uniqueListNSeed[Char])

      property("should always return AnyVal containers of the required size with Arb") =
        containerWithSizedSeedArbProp[AnyVal](G.uniqueListNSeed[AnyVal])

      property("should always return Int containers of the required size with Gen") =
        containerWithSizedSeedGenProp[Int](G.uniqueListNSeed[Int])

      property("should always return Char containers of the required size with Gen") =
        containerWithSizedSeedGenProp[Char](G.uniqueListNSeed[Char])

      property("should always return AnyVal containers of the required size with Gen") =
        containerWithSizedSeedGenProp[AnyVal](G.uniqueListNSeed[AnyVal])
    }
  }


}
