val CatsEffectVersion = "1.1.0"
val CirceVersion = "0.10.1"
val Http4sVersion = "0.20.0-M4"
val LogbackVersion = "1.2.3"
val kafkaClientsVersion = "1.0.1"
val fs2Version = "1.0.0"

lazy val commonSettings = Seq(
  organization := "com.alaphi",
  name := "AccountServiceIO",
  version := "0.1",
  scalaVersion := "2.12.8",
  resolvers += "kafka-clients" at "https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients",
  libraryDependencies ++= Seq(
    "org.typelevel"    %% "cats-effect"          % CatsEffectVersion,
    "org.http4s"       %% "http4s-blaze-server"  % Http4sVersion,
    "org.http4s"       %% "http4s-blaze-client"  % Http4sVersion,
    "org.http4s"       %% "http4s-circe"         % Http4sVersion,
    "org.http4s"       %% "http4s-dsl"           % Http4sVersion,
    "io.circe"         %% "circe-core"           % CirceVersion,
    "io.circe"         %% "circe-generic"        % CirceVersion,
    "io.circe"         %% "circe-generic-extras" % CirceVersion,
    "io.circe"         %% "circe-parser"         % CirceVersion,
    "co.fs2"           %% "fs2-core"             % fs2Version,
    "co.fs2"           %% "fs2-io"               % fs2Version,
    "ch.qos.logback"   %  "logback-classic"      % LogbackVersion,
    "org.apache.kafka" %  "kafka-clients"        % kafkaClientsVersion
  )
)

lazy val dockerSettings = Seq(
  dockerBaseImage := "openjdk:jre-alpine",
  dockerUpdateLatest := true
)

lazy val root = (project in file("."))
  .aggregate(common, accountServer, kafkaClient, kafkaSerdes)

lazy val common = (project in file("common"))
  .settings(commonSettings)

lazy val accountServer = (project in file("accountserver"))
  .enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin)
  .dependsOn(common, kafkaClient, kafkaSerdes)
  .settings(dockerSettings)

lazy val kafkaClient = (project in file("kafkaclient"))
  .dependsOn(common, kafkaSerdes)
  .settings(dockerSettings)

lazy val kafkaSerdes = (project in file("kafkaserdes"))
  .dependsOn(common)
  .settings(dockerSettings)