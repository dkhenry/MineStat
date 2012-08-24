resolvers ++= Seq(
    DefaultMavenRepository,
    Resolver.url("Play", url("http://download.playframework.org/ivy-releases/")) (Resolver.ivyStylePatterns),
	Resolver.url("sbt-plugin-releases", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.8.3")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.1.0-M2")

addSbtPlugin("org.ensime" % "ensime-sbt-cmd" % "0.1.0")

// For 0.12 
// libraryDependencies += Defaults.sbtPluginExtra("com.eed3si9n" % "sbt-assembly" % "0.7.3", "0.12.0-M2", "2.9.1")
