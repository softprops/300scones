name := "300scones"

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-netty-server" % "0.6.1",
  "net.databinder.dispatch" %% "core" % "0.9.0-alpha5"
)

seq(heroic.Plugin.heroicSettings: _*)
