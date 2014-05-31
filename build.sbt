name := "SitemapCreator"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "commons-io" % "commons-io" % "2.4",
  "org.jsoup" % "jsoup" % "1.7.3"
)

play.Project.playJavaSettings
