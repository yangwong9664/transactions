name := "transactions"

version := "0.1"

scalaVersion := "2.12.3"

scalaSource in Compile := baseDirectory.value / "src"
scalaSource in Test := baseDirectory.value / "test"
resourceDirectory in Compile := baseDirectory.value / "conf"

parallelExecution in Test := false
fork in Test := true

libraryDependencies ++= {
  Seq(
    "org.scalatest" % "scalatest_2.12" % "3.0.4" % "test",
    "com.google.inject" % "guice" % "4.2.2"
  )
}
