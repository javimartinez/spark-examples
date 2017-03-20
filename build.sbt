// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `spark-examples` =
  project
    .in(file("."))
    .enablePlugins(GitVersioning)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.sparkCore,
        library.sparkSql,
        library.cassandraConnector,
        library.scalaCheck % Test,
        library.scalaTest % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {

    object Version {
      val scalaCheck = "1.13.4"
      val scalaTest = "3.0.1"
      val sparkVersion = "2.1.0"
    }

    val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
    val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest
    val sparkCore = "org.apache.spark" %% "spark-core" % Version.sparkVersion
    val sparkSql = "org.apache.spark" %% "spark-sql" % Version.sparkVersion
    val cassandraConnector = "com.datastax.spark" %% "spark-cassandra-connector" % "2.0.0"

  }

// *****************************************************************************
// Settings
// *****************************************************************************        |

lazy val settings =
  commonSettings ++
    scalafmtSettings ++
    gitSettings

lazy val commonSettings =
  Seq(
    scalaVersion := "2.11.8",
    crossScalaVersions := Seq(scalaVersion.value, "2.11.8"),
    organization := "com.jmartinez",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8"
    ),
    javacOptions ++= Seq(
      "-source", "1.8",
      "-target", "1.8"
    ),
    unmanagedSourceDirectories.in(Compile) :=
      Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) :=
      Seq(scalaSource.in(Test).value)
  )

lazy val scalafmtSettings =
  reformatOnCompileSettings ++
    Seq(
      formatSbtFiles := false,
      scalafmtConfig :=
        Some(baseDirectory.in(ThisBuild).value / ".scalafmt.conf"),
      ivyScala :=
        ivyScala.value.map(_.copy(overrideScalaVersion = sbtPlugin.value)) // TODO Remove once this workaround no longer needed (https://github.com/sbt/sbt/issues/2786)!
    )

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )
