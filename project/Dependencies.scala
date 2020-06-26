import sbt._

object Dependencies {

  lazy val tapir = "com.softwaremill.sttp.tapir" %% "tapir-core" % "0.16.1"
  lazy val tapirHttp4s = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "0.16.1"
  lazy val tapirOpenApi = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % "0.16.1"
  lazy val tapirOpenApiCircle = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % "0.16.1"
  lazy val tapirHttp4sSwagger = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s" % "0.16.1"
  lazy val tapirCircle = "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "0.16.1"
  lazy val circleLiteral = "io.circe" %% "circe-literal" % "0.13.0"
  lazy val circleGenericExtras = "io.circe" %% "circe-generic-extras" % "0.13.0"
  lazy val monix = "io.monix" %% "monix" % "3.2.2"

  lazy val scalaTest = "org.scalatest" % "scalatest_2.13" % "3.2.0"
  lazy val scalaMock = "org.scalamock" %% "scalamock" % "4.4.0"
}
