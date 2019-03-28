lazy val commonSettings = Seq(
  organization := "edu.berkeley.cs",
  version := "1.0",
  scalaVersion := "2.12.4",
  traceLevel := 15,
  test in assembly := {},
  assemblyMergeStrategy in assembly := { _ match {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case _ => MergeStrategy.first}},
  scalacOptions ++= Seq("-deprecation","-unchecked","-Xsource:2.11"),
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.3",
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  libraryDependencies += "edu.berkeley.cs" %% "firrtl-interpreter" % "1.2-SNAPSHOT",
  libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.1",
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    Resolver.mavenLocal))

lazy val rocketchip = RootProject(file("rocket-chip"))

lazy val testchipip = project.settings(commonSettings)
  .dependsOn(rocketchip)

lazy val hwacha = project.settings(commonSettings)
  .dependsOn(rocketchip)

// Checks for -DROCKET_USE_MAVEN.
// If it's there, use a maven dependency.
// Else, depend on subprojects in git submodules.
def conditionalDependsOn(prj: Project): Project = {
  if (sys.props.contains("ROCKET_USE_MAVEN")) {
    prj.settings(Seq(
      libraryDependencies += "edu.berkeley.cs" %% "testchipip" % "1.0-020719-SNAPSHOT",
    ))
  } else {
    prj.dependsOn(testchipip)
  }
}
lazy val example = conditionalDependsOn(project in file("."))
  .settings(commonSettings)
  .dependsOn(hwacha)

lazy val tapeout = conditionalDependsOn(project in file("./barstools/tapeout/"))
  .settings(commonSettings)

lazy val mdf = (project in file("./barstools/mdf/scalalib/"))

lazy val `barstools-macros` = conditionalDependsOn(project in file("./barstools/macros/"))
  .enablePlugins(sbtassembly.AssemblyPlugin)
  .settings(commonSettings)
  .dependsOn(mdf)

