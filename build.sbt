ThisBuild / organization := "io.github.ekuzmichev"
ThisBuild / organizationName := "ekuzmichev"
ThisBuild / organizationHomepage := Some(url("https://github.com/ekuzmichev"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/ekuzmichev/circe-scala-bson"),
    "scm:git@github.com:ekuzmichev/circe-scala-bson.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "ekuzmichev",
    name = "Evgenii Kuzmichev",
    email = "evgenii.e.kuzmichev@gmail.com",
    url = url("https://github.com/ekuzmichev")
  )
)

ThisBuild / description := "Conversions for circe and Scala bson (Scala wrapper / extension to the bson library)"
ThisBuild / licenses := List("MIT" -> url("http://opensource.org/licenses/MIT"))
ThisBuild / homepage := Some(url("https://github.com/ekuzmichev/circe-scala-bson"))

ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true

ThisBuild / scalaVersion := "2.12.8"
ThisBuild / crossScalaVersions := Seq("2.12.8", "2.13.1")
ThisBuild / scalacOptions ++= scalacOptionsVersion(scalaVersion.value)
ThisBuild / releaseCrossBuild := true

val crossScalaOptions = Seq("-unchecked", "-deprecation")
def scalacOptionsVersion(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, 13)) => crossScalaOptions
    case _             => crossScalaOptions :+ "-Ypartial-unification"
  }

lazy val root = (project in file("."))
  .settings(
    name := "circe-scala-bson",
    libraryDependencies ++= Seq(
      libs.catsCore,
      libs.circeCore,
      libs.mongoScalaBson,
      libs.circeGeneric % Test,
      libs.circeParser  % Test,
      libs.scalaTest    % Test
    ),
    releasePublishArtifactsAction := PgpKeys.publishSigned.value
  )

lazy val libs = new {
  val catsV           = "2.1.0"
  val circeV          = "0.13.0"
  val mongoScalaBsonV = "4.0.2"
  val scalaTestV      = "3.1.1"

  val catsCore       = "org.typelevel"     %% "cats-core"        % catsV
  val circeCore      = "io.circe"          %% "circe-core"       % circeV
  val circeGeneric   = "io.circe"          %% "circe-generic"    % circeV
  val circeParser    = "io.circe"          %% "circe-parser"     % circeV
  val mongoScalaBson = "org.mongodb.scala" %% "mongo-scala-bson" % mongoScalaBsonV
  val scalaTest      = "org.scalatest"     %% "scalatest"        % scalaTestV
}
