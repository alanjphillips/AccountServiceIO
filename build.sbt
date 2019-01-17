val CatsEffectVersion = "1.1.0"
val CirceVersion = "0.10.1"
val Http4sVersion = "0.20.0-M4"
val LogbackVersion = "1.2.3"

lazy val commonSettings = Seq(
  organization := "com.alaphi",
  name := "AccountServiceIO",
  version := "0.1",
  scalaVersion := "2.12.8",
  libraryDependencies ++= Seq(
    "org.typelevel"   %% "cats-effect"          % CatsEffectVersion,
    "org.http4s"      %% "http4s-blaze-server"  % Http4sVersion,
    "org.http4s"      %% "http4s-blaze-client"  % Http4sVersion,
    "org.http4s"      %% "http4s-circe"         % Http4sVersion,
    "org.http4s"      %% "http4s-dsl"           % Http4sVersion,
    "ch.qos.logback"  %  "logback-classic"      % LogbackVersion,
    "io.circe"        %% "circe-core"           % CirceVersion,
    "io.circe"        %% "circe-generic"        % CirceVersion,
    "io.circe"        %% "circe-generic-extras" % CirceVersion,
    "io.circe"        %% "circe-parser"         % CirceVersion
  )
)

lazy val dockerSettings = Seq(
  dockerBaseImage := "openjdk:jre-alpine",
  dockerUpdateLatest := true
)

lazy val root = (project in file("."))
  .aggregate(common, accountserver)

lazy val common = (project in file("common"))
  .settings(commonSettings)

lazy val accountserver = (project in file("accountserver"))
  .enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin)
  .dependsOn(common)
  .settings(dockerSettings)