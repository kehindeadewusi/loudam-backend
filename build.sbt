organization in ThisBuild := "com.loudam"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test

lazy val `loudam` = (project in file("."))
  .aggregate(`incidence-api`, `incidence-impl`)

lazy val `incidence-api` = (project in file("incidence-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `incidence-impl` = (project in file("incidence-impl"))
  .enablePlugins(LagomScala, PlayScala)
  .disablePlugins(PlayLayoutPlugin)
   .settings( routesGenerator := InjectedRoutesGenerator)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`incidence-api`)

