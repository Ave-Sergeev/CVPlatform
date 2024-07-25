addSbtPlugin("org.scalameta"  % "sbt-scalafmt" % "2.4.3")
addSbtPlugin("ch.epfl.scala"  % "sbt-scalafix" % "0.11.0")
addSbtPlugin("com.thesamet"   % "sbt-protoc"   % "1.0.6")
addSbtPlugin("org.xerial.sbt" % "sbt-pack"     % "0.20")

libraryDependencies += "com.thesamet.scalapb"          %% "compilerplugin"   % "0.11.15"
libraryDependencies += "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % "0.6.1"
