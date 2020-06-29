import sbt._

object Dependencies {
  lazy val doobieVersion = "0.8.8"
  lazy val testContainersVersion = "1.14.3"
  lazy val testContainersPostgreSQLVersion = "0.37.0"

  lazy val tapir = "com.softwaremill.sttp.tapir" %% "tapir-core" % "0.16.1"
  lazy val tapirHttp4s = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "0.16.1"
  lazy val tapirOpenApi = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % "0.16.1"
  lazy val tapirOpenApiCircle = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % "0.16.1"
  lazy val tapirHttp4sSwagger = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s" % "0.16.1"
  lazy val tapirCircle = "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "0.16.1"
  lazy val circleLiteral = "io.circe" %% "circe-literal" % "0.13.0"
  lazy val circleGenericExtras = "io.circe" %% "circe-generic-extras" % "0.13.0"
  lazy val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.13.0"
  lazy val flyway = "org.flywaydb" % "flyway-core" % "6.5.0"
  lazy val testContainers = "org.testcontainers" % "testcontainers" % testContainersVersion
  lazy val testContainersScalaTest = "com.dimafeng" %% "testcontainers-scala-scalatest" % testContainersPostgreSQLVersion
  lazy val testContainersPostgreSQL = "com.dimafeng" %% "testcontainers-scala-postgresql" % testContainersPostgreSQLVersion

  lazy val doobie = Seq(
    "org.tpolecat" %% "doobie-core" % doobieVersion,
    "org.tpolecat" %% "doobie-postgres" % doobieVersion,
    "org.tpolecat" %% "doobie-scalatest" % doobieVersion % Test
  )

  lazy val scalaTest = "org.scalatest" % "scalatest_2.13" % "3.2.0"
  lazy val scalaMock = "org.scalamock" %% "scalamock" % "4.4.0"
}
