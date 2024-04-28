val projectName    = "CertVerifyPlatform"
val projectVersion = "0.1.3"

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

lazy val root = (project in file("."))
  .enablePlugins(ScalafixPlugin)
  .settings(
    libraryDependencies ++= Dependencies.globalProjectDependencies,
    Compile / scalacOptions ++= Settings.compilerOptions,
    scalafmtSettings
  )
