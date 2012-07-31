import AssemblyKeys._

name := "MineStat"

version :="1.0"

libraryDependencies ++= Seq(
  "com.mongodb.casbah" %% "casbah" % "2.1.5-1",   
  "com.codahale" %% "jerkson" % "0.5.0",
  "joda-time" % "joda-time" % "2.0",
  "org.joda" % "joda-convert" % "1.1",
  "org.bukkit" % "craftbukkit" % "1.2.3-R0.2",
  "javax.servlet" % "servlet-api" % "2.5" % "provided"
)

autoCompilerPlugins := true

addCompilerPlugin("org.scala-lang.plugins" % "continuations" % "2.9.1")

scalacOptions += "-P:continuations:enable"

seq(assemblySettings: _*)

jarName in assembly := "MineStat.jar"

test in assembly := {}

excludedJars in assembly <<= (fullClasspath in assembly) map { cp => 
  cp filter {
  	_.data.getName == "craftbukkit-1.2.3-R0.2.jar"   	
  }
}

resolvers += "Local Maven2 Repository" at "file:///home/dkozlowski/.m2/repository/"

resolvers += "Local Maven2 Repository" at "file:///home/dan/.m2/repository/"

resolvers += "repo.codahale.com" at "http://repo.codahale.com"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Bukkit" at "http://repo.bukkit.org/content/repositories/releases/"
