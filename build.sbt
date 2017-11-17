organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

lazy val `kafka-message-test` = (project in file("."))
  .aggregate(
    `kafka-message-generator-api`,
    `kafka-message-generator-impl`,
    `kafka-message-consumer-alpha-api`,
    `kafka-message-consumer-alpha-impl`,
    `kafka-message-consumer-beta-api`,
    `kafka-message-consumer-beta-impl`
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

lazy val `kafka-message-consumer-alpha-api` = (project in file("kafka-message-consumer-alpha-api"))
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi
    )
  )

lazy val `kafka-message-consumer-alpha-impl` = (project in file("kafka-message-consumer-alpha-impl"))
  .enablePlugins(LagomJava)
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslKafkaClient
    )
  )
  .dependsOn(`kafka-message-consumer-alpha-api`, `kafka-message-generator-api`)

lazy val `kafka-message-consumer-beta-api` = (project in file("kafka-message-consumer-beta-api"))
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi
    )
  )

lazy val `kafka-message-consumer-beta-impl` = (project in file("kafka-message-consumer-beta-impl"))
  .enablePlugins(LagomJava)
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslKafkaClient
    )
  )
  .dependsOn(`kafka-message-consumer-beta-api`, `kafka-message-generator-api`)

val lombok = "org.projectlombok" % "lombok" % "1.16.10"

def common = Seq(
  javacOptions in compile += "-parameters"
)


// use an external Kafka server so we can control the exact version on the broker.
lagomKafkaEnabled in ThisBuild := false
