
val scala3Version = "3.0.0"

// library name
name := "scala-latex"

// library version
version := "1.1.0"

/////////////////////////////////////////////////////////////////
// begin maven etc. publishing information

// groupId, SCM, license information
organization := "org.maraist"
homepage := Some(url("https://github.com/jphmrst/scala-latex"))
scmInfo := Some(ScmInfo(
  url("https://github.com/jphmrst/scala-latex"),
  "git@github.com:jphmrst/scala-latex.git"))
developers := List(Developer(
  "jphmrst", "jphmrst", "via-github@maraist.org",
  url("https://maraist.org/work/")))
licenses += (
  "Educational",
  url("https://github.com/jphmrst/scala-latex/blob/master/LICENSE.txt"))

// disable publish with scala version, otherwise artifact name will
// include scala version
// e.g cassper_2.11
crossPaths := false

// add sonatype repository settings
// snapshot versions publish to sonatype snapshot repository
// other versions publish to sonatype staging repository
pomIncludeRepository := { _ => false }
val nexus = "https://s01.oss.sonatype.org/"
publishTo := {
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
publishMavenStyle := true

ThisBuild / versionScheme := Some("semver-spec")

// end of maven etc. publishing section
/////////////////////////////////////////////////////////////////

Global / excludeLintKeys ++= Set(scalacOptions)
Compile / doc / scalacOptions ++= Seq(
  // "-groups",
  "-doc-root-content", "src/root.scaladoc"
)

lazy val main = project
  .in(file("."))
  .settings(
    scalaVersion := scala3Version,
    compile / watchTriggers += baseDirectory.value.toGlob / "build.sbt",
    unmanagedSources / excludeFilter := ".#*",
    scalacOptions ++= Seq( "-source:future-migration" ),
  )
