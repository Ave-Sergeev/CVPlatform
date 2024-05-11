val projectName    = "CertVerifyPlatform"
val projectVersion = "1.0.0"

name         := projectName
version      := projectVersion
scalaVersion := Dependencies.Version.scala

def scalafmtSettings = Seq(
  Compile / compile := (Compile / compile)
    .dependsOn(
      Compile / scalafmtCheckAll,
      Compile / scalafmtSbtCheck
    )
    .value
)

resolvers ++= List(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

lazy val root = (project in file("."))
  .enablePlugins(ScalafixPlugin)
  .settings(
    libraryDependencies ++= Dependencies.globalProjectDependencies,
    Compile / scalacOptions ++= Settings.compilerOptions,
    scalafmtSettings
  )
