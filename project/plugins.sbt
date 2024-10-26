addSbtPlugin("org.scalameta"  % "sbt-scalafmt" % "2.5.2")
addSbtPlugin("ch.epfl.scala"  % "sbt-scalafix" % "0.12.1")
addSbtPlugin("com.thesamet"   % "sbt-protoc"   % "1.0.7")
addSbtPlugin("org.xerial.sbt" % "sbt-pack"     % "0.20")

libraryDependencies += "com.thesamet.scalapb"          %% "compilerplugin"   % "0.11.17"
libraryDependencies += "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % "0.6.2"
