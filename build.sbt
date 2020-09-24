import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val catsV = "2.2.0"

val specs2V = "4.10.1"

val kindProjectorV = "0.11.0"
val betterMonadicForV = "0.3.1"


// val dottyV = "0.27.0-RC1"
val dottyV = "0.28.0-bin-20200921-18578b3-NIGHTLY"

// Projects
lazy val `nonemptystring` = project.in(file("."))
  .disablePlugins(MimaPlugin)
  .settings(skip in publish := true)
  .aggregate(core.jvm, core.js, tests.jvm, tests.js)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core"))
  .settings(
    name := "nonemptystring",
    scalaVersion := dottyV,
    crossScalaVersions := Seq(dottyV, "2.13.3", "2.12.12"),

    libraryDependencies ++= {
      if (isDotty.value) Nil
      else Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
    },
    Compile / unmanagedSourceDirectories ++= {
      val major = if (isDotty.value) "-3" else "-2"
      List(CrossType.Pure, CrossType.Full).flatMap(
        _.sharedSrcDir(baseDirectory.value, "main").toList.map(f => file(f.getPath + major))
      )
    }
  )


// lazy val sc2 = crossProject(JSPlatform, JVMPlatform)
//   .crossType(CrossType.Pure)
//   .in(file("modules/sc2"))
//   .settings(
//     name := "nonemptystring",
//     scalaVersion := "2.13.3",
//     crossScalaVersions := Seq(scalaVersion.value, "2.12.12"),

//     libraryDependencies ++= {
//       if (isDotty.value) Nil
//       else Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
//     },
//   )

// lazy val dotty = project.in(file("modules/dotty"))
//   .settings(
//     name := "nonemptystring",
//     scalaVersion := dottyV
//   )

lazy val tests = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/tests"))
  .settings(
    skip in publish := true,
    scalaVersion := dottyV,
    crossScalaVersions := Seq(dottyV, "2.13.3", "2.12.12"),
  ).dependsOn(core)
  /*
  HOW TO DO THIS
  .dependsOn(
    {
      if (isDotty.value) dotty.dependencies
      else sc2.jvm.dependencies
    }:_*
  )
  */

lazy val site = project.in(file("site"))
  .disablePlugins(MimaPlugin)
  .enablePlugins(MicrositesPlugin)
  .enablePlugins(MdocPlugin)
  .settings(skip in publish := true)
  .dependsOn(core.jvm)
  .settings{
    import microsites._
    Seq(
      micrositeName := "nonemptystring",
      micrositeDescription := "NonEmpty String",
      micrositeAuthor := "Christopher Davenport",
      micrositeGithubOwner := "ChristopherDavenport",
      micrositeGithubRepo := "nonemptystring",
      micrositeBaseUrl := "/nonemptystring",
      micrositeDocumentationUrl := "https://www.javadoc.io/doc/io.chrisdavenport/nonemptystring_2.13",
      micrositeGitterChannelUrl := "ChristopherDavenport/libraries", // Feel Free to Set To Something Else
      micrositeFooterText := None,
      micrositeHighlightTheme := "atom-one-light",
      micrositePalette := Map(
        "brand-primary" -> "#3e5b95",
        "brand-secondary" -> "#294066",
        "brand-tertiary" -> "#2d5799",
        "gray-dark" -> "#49494B",
        "gray" -> "#7B7B7E",
        "gray-light" -> "#E5E5E6",
        "gray-lighter" -> "#F4F3F4",
        "white-color" -> "#FFFFFF"
      ),
      micrositeCompilingDocsTool := WithMdoc,
      scalacOptions in Tut --= Seq(
        "-Xfatal-warnings",
        "-Ywarn-unused-import",
        "-Ywarn-numeric-widen",
        "-Ywarn-dead-code",
        "-Ywarn-unused:imports",
        "-Xlint:-missing-interpolator,_"
      ),
      micrositePushSiteWith := GitHub4s,
      micrositeGithubToken := sys.env.get("GITHUB_TOKEN"),
      micrositeExtraMdFiles := Map(
          file("CODE_OF_CONDUCT.md")  -> ExtraMdFileConfig("code-of-conduct.md",   "page", Map("title" -> "code of conduct",   "section" -> "code of conduct",   "position" -> "100")),
          file("LICENSE")             -> ExtraMdFileConfig("license.md",   "page", Map("title" -> "license",   "section" -> "license",   "position" -> "101"))
      )
    )
  }

// General Settings

// General Settings
inThisBuild(List(
  organization := "io.chrisdavenport",
  developers := List(
    Developer("ChristopherDavenport", "Christopher Davenport", "chris@christopherdavenport.tech", url("https://github.com/ChristopherDavenport"))
  ),

  homepage := Some(url("https://github.com/ChristopherDavenport/nonemptystring")),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),

  pomIncludeRepository := { _ => false},
  scalacOptions in (Compile, doc) ++= Seq(
      "-groups",
      "-sourcepath", (baseDirectory in LocalRootProject).value.getAbsolutePath,
      "-doc-source-url", "https://github.com/ChristopherDavenport/nonemptystring/blob/v" + version.value + "€{FILE_PATH}.scala"
  )
))