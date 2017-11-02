package net.ssanj.scalacheck.ease

import org.scalacheck.Prop
import org.scalacheck.Properties

final class XProperty(outer: Properties) {
  def update(propName: String, p: => Prop): Unit = {
    outer.property(s"$propName ${AnsiColours.red("[Ignored]")}") = Prop.passed
  }
}

/**
 * Allows skipping properties similar that allowed by the Mocha framework in Javascript.
 * Mix in this trait to get access to `xproperty` which wraps any property and converts
 * it to a property that passes. It would be better if ScalaCheck supported some sort of
 * "ignored" Prop Status, where it would be brought to the developers attention in the
 * test report. Unfortunately this is not the case and we rely on terminal colours and
 * the text "ignored" to highlight ignored properties.
 *
 * @example:{{{
 *   property("some property name") = //failing Prop you want to ignore
 * }}}
 *
 * can be converted to a passing Prop by adding an 'x' in front of property
 *
 * @example:{{{
 *   xproperty("some property name") = //failing Prop you want to ignore
 * }}}
 */
trait XProperties { self: Properties =>
  lazy val xproperty = new XProperty(self)
}

object AnsiColours {
  val ANSI_RED   = "\u001B[31m";
  val ANSI_RESET = "\u001B[0m";

  def red(value: String): String = s"${ANSI_RED}${value}${ANSI_RESET}"
}

