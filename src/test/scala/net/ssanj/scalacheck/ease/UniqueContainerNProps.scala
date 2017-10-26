package net.ssanj.scalacheck.ease

import org.scalacheck.Properties
import org.scalacheck._

object UniqueContainerNProps extends Properties("UniqueContainerN") {

  include(UniqueListNProps.properties)
  include(UniqueListNRSizedProps.properties)
  include(UniqueListNSizedProps.properties)
  include(UniqueListNSeedProps.properties)
}