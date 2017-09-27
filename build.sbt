name := "scalacheck-ease"

organization := "net.ssanj"

version := "0.0.1"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "org.scalatest"  %% "scalatest"   % "3.0.1"  % "test",
  "org.scalacheck" %% "scalacheck"  % "1.13.5" % "test"
)

scalacOptions ++= Seq(
                      "-unchecked",
                      "-deprecation",
                      "-feature",
                      "-Xfatal-warnings",
                      "-Xlint:_",
                      "-Ywarn-dead-code",
                      "-Ywarn-inaccessible",
                      "-Ywarn-unused-import",
                      "-Ywarn-infer-any",
                      "-Ywarn-nullary-override",
                      "-Ywarn-nullary-unit"
                     )

scalacOptions in (Compile, console) ~= (_.filterNot(Seq("-Xfatal-warnings", "-Ywarn-unused-import", "-Xlint:_") contains _))

scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value

initialCommands in (Test, console) := "import org.scalacheck._, net.ssanj.scalacheck.ease._"

