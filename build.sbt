import sbtprotoc.ProtocPlugin.ProtobufConfig

val projectName    = "CVPlatform"
val projectVersion = "1.2.0"

scalaVersion := Dependencies.Version.scala

Compile / PB.targets := Seq(
  scalapb.gen(grpc = true)          -> (Compile / sourceManaged).value / "protobuf",
  scalapb.zio_grpc.ZioCodeGenerator -> (Compile / sourceManaged).value / "grpc"
)

Compile / PB.protocVersion := "-v3.25.4"

Test / PB.protoSources ++= (Compile / PB.protoSources).value

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

def scalaFixSettings = Seq(
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)

def scalafmtSettings = Seq(
  Compile / compile := (Compile / compile)
    .dependsOn(
      Compile / scalafmtCheckAll,
      Compile / scalafmtSbtCheck
    )
    .value
)

lazy val root = (project in file("."))
  .enablePlugins(ProtocPlugin, PackPlugin)
  .enablePlugins(ScalafixPlugin)
  .in(file("."))
  .settings(
    name         := projectName,
    version      := projectVersion,
    scalaVersion := Dependencies.Version.scala,
    libraryDependencies ++= Dependencies.globalProjectDependencies,
    Compile / scalacOptions ++= Settings.compilerOptions,
    Compile / console / scalacOptions --= Seq("-Xlint"),
    scalafmtSettings,
    scalaFixSettings,
    ProtobufConfig / javaSource := ((Compile / sourceDirectory).value / "generated")
  )
