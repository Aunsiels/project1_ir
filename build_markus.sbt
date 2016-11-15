name := "project_ir1"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies  ++= Seq(
  // Last snapshot
  "org.scalanlp" %% "breeze" % "0.12"

  // Native libraries are not included by default. add this if you want them (as of 0.7)
  // Native libraries greatly improve performance, but increase jar sizes.
  // It also packages various blas implementations, which have licenses that may or may not
  // be compatible with the Apache License. No GPL code, as best I know.
  , "org.scalanlp" %% "breeze-natives" % "0.12"

  // The visualization library is distributed separately as well.
  // It depends on LGPL code.
  //, "org.scalanlp" %% "breeze-viz" % "0.12"
)

//javaOptions in run += "-Xms256M -Xmx2G"

resolvers ++= Seq(
  // other resolvers here
  // if you want to use snapshot builds (currently 0.8-SNAPSHOT), use this.
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)
