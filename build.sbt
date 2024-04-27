ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

val projectName = "CertVerificationPlatform"

name:= projectName

lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Dependencies.globalProjectDependencies,
    Compile / scalacOptions ++= Settings.compilerOptions,
  )
