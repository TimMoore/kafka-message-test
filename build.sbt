organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

lazy val `kafka-message-test` = (project in file("."))
  .aggregate(
    `kafka-message-generator-api`,
    `kafka-message-generator-impl`,
    `kafka-message-consumer-api`,
    `kafka-message-consumer-impl`
  )

lazy val `kafka-message-generator-api` = (project in file("kafka-message-generator-api"))
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )

lazy val `kafka-message-generator-impl` = (project in file("kafka-message-generator-impl"))
  .enablePlugins(LagomJava)
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslKafkaBroker,
      lagomJavadslTestKit,
      lombok
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`kafka-message-generator-api`)

lazy val `kafka-message-consumer-api` = (project in file("kafka-message-consumer-api"))
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi
    )
  )

lazy val `kafka-message-consumer-impl` = (project in file("kafka-message-consumer-impl"))
  .enablePlugins(LagomJava)
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslKafkaClient,
      lagomJavadslTestKit
    )
  )
  .dependsOn(`kafka-message-consumer-api`, `kafka-message-generator-api`)

val lombok = "org.projectlombok" % "lombok" % "1.16.10"

def common = Seq(
  javacOptions in compile += "-parameters"
)

