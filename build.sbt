name := "K8STest"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.14",
  "com.typesafe.akka" %% "akka-remote" % "2.5.14",
  "io.kubernetes" % "client-java" % "2.0.0",
  "org.slf4j" % "slf4j-simple" % "1.7.25"
)

// Fix SBT de-duplication errors
assemblyMergeStrategy in assembly := {
  case PathList("org", "bouncycastle", _*) => MergeStrategy.first // If it's a org.bouncycastle path,
                                                                  // then choose the first one
  case x =>                                                       // Otherwise, just use the default
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

// We don't want a main class specified
mainClass in Compile := None
mainClass in assembly := None