import Dependencies._

val Http4sVersion = "0.21.4"
val CirceVersion = "0.13.0"
val Specs2Version = "4.9.3"
val LogbackVersion = "1.2.3"

lazy val root = (project in file("."))
  .settings(
    organization := "com.github.apien",
    name := "tic-tac-toe",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.6",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      tapir,
      tapirHttp4s,
      tapirOpenApi,
      tapirOpenApiCircle,
      tapirHttp4sSwagger,
      tapirCircle,
      circleLiteral,
      circleGenericExtras,
      pureConfig,
      flyway,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      testContainers % Test,
      testContainersScalaTest % Test,
      testContainersPostgreSQL % Test,
      scalaTest % Test,
      scalaMock % Test
    ) ++ doobie,
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings"
)

enablePlugins(FlywayPlugin)
