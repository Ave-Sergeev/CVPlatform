import sbtprotoc.ProtocPlugin.ProtobufConfig

val projectName    = "CVPlatform"
val projectVersion = "1.1.1"

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

resolvers ++= List(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

Compile / PB.targets := Seq(
  scalapb.gen(grpc = true)          -> (Compile / sourceManaged).value / "protobuf",
  scalapb.zio_grpc.ZioCodeGenerator -> (Compile / sourceManaged).value / "grpc"
)

Compile / PB.protocVersion := "-v3.24.4"

Test / PB.protoSources ++= (Compile / PB.protoSources).value

lazy val root = (project in file("."))
  .enablePlugins(ProtocPlugin, PackPlugin)
  .enablePlugins(ScalafixPlugin)
  .settings(
    name         := projectName,
    version      := projectVersion,
    scalaVersion := Dependencies.Version.scala,
    libraryDependencies ++= Dependencies.globalProjectDependencies,
    Compile / scalacOptions ++= Settings.compilerOptions,
    scalafmtSettings,
    scalaFixSettings,
    ProtobufConfig / javaSource := ((Compile / sourceDirectory).value / "generated")
  )
