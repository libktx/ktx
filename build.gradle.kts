import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    jcenter()
    mavenCentral()
  }

  val dokkaVersion: String by project
  val kotlinVersion: String by project
  val nexusPluginVersion: String by project
  val junitPlatformVersion: String by project
  val configurationsPluginVersion: String by project

  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    classpath("io.codearte.gradle.nexus:gradle-nexus-staging-plugin:$nexusPluginVersion")
    classpath("com.netflix.nebula:gradle-extra-configurations-plugin:$configurationsPluginVersion")
    classpath("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
    classpath("org.junit.platform:junit-platform-gradle-plugin:$junitPlatformVersion")
  }
}

val ktlintVersion: String by project

val libGroup: String by project
val ossrhUsername: String by project
val ossrhPassword: String by project

plugins {
  java
  distribution
  id("io.codearte.nexus-staging") version "0.22.0"
}

repositories {
  jcenter()
}

val libVersion = file("version.txt").readText()

allprojects {
  configurations.create("linter")

  dependencies {
    "linter"("com.pinterest:ktlint:$ktlintVersion")
  }
}

subprojects {
  apply(plugin = "maven")
  apply(plugin = "java")
  apply(plugin = "kotlin")
  apply(plugin = "signing")
  apply(plugin = "nebula.provided-base")
  apply(plugin = "org.jetbrains.dokka")
  apply(plugin = "jacoco")

  val isReleaseVersion = !libVersion.endsWith("SNAPSHOT")

  repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
  }

  group = libGroup
  version = libVersion
  val archivesBaseName = project.name

  java {
    sourceCompatibility = JavaVersion.VERSION_1_6
    targetCompatibility = JavaVersion.VERSION_1_6
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = "1.6"
      freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
  }

  val gdxVersion: String by project
  val junitVersion: String by project
  val kotlinVersion: String by project
  val kotlinTestVersion: String by project
  val kotlinMockitoVersion: String by project

  dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    compileOnly("com.badlogicgames.gdx:gdx:$gdxVersion")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("io.kotlintest:kotlintest:$kotlinTestVersion")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:$kotlinMockitoVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
  }

  tasks.register("lint", JavaExec::class) {
    description = "Check Kotlin code style."
    group = "verification"
    main = "com.pinterest.ktlint.Main"
    classpath = configurations["linter"]
    args = listOf("src/**/*.kt")

    tasks["check"].dependsOn(this)
  }

  tasks.register("format", JavaExec::class) {
    description = "Fix Kotlin code style."
    group = "formatting"
    main = "com.pinterest.ktlint.Main"
    classpath = configurations["linter"]
    args = listOf("-F", "src/**/*.kt")
  }

  tasks.withType<Test> {
    testLogging {
      events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.STANDARD_OUT)
      exceptionFormat = TestExceptionFormat.FULL
      showExceptions = true
      showCauses = true
      showStackTraces = true

      debug {
        events = setOf(
          TestLogEvent.STARTED,
          TestLogEvent.FAILED,
          TestLogEvent.PASSED,
          TestLogEvent.SKIPPED,
          TestLogEvent.STANDARD_ERROR,
          TestLogEvent.STANDARD_OUT
        )
        exceptionFormat = TestExceptionFormat.FULL
      }

      info.events = debug.events
      info.exceptionFormat = debug.exceptionFormat
    }
  }

  tasks.named<Jar>("jar") {
//    from(the<SourceSetContainer>()["main"].output)
    archiveBaseName.set(archivesBaseName)
  }

  val dokka by tasks.getting

  tasks.register<Zip>("dokkaZip") {
    from("$buildDir/dokka")
    dependsOn(dokka)
  }

  val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from("$buildDir/dokka")
    dependsOn(dokka)
  }

  val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
//    from(the<SourceSetContainer>()["main"].allSource)
  }

  artifacts {
    archives(javadocJar)
    archives(sourcesJar)
  }

  tasks.withType<Sign> {
    onlyIf { isReleaseVersion && gradle.taskGraph.hasTask("uploadArchives")}
  }

  configure<SigningExtension> {
    sign(configurations.archives.get())
  }

  val uploadSnapshot by tasks.registering

  if (!isReleaseVersion) {
    uploadSnapshot.configure { finalizedBy(tasks["uploadArchives"]) }
  }

  tasks.named<Upload>("uploadArchives") {
    repositories {
      withConvention(MavenRepositoryHandlerConvention::class) {
        mavenDeployer {
          beforeDeployment { the<SigningExtension>().signPom(this) }

          withGroovyBuilder {
            "repository"("url" to "https://oss.sonatype.org/service/local/staging/deploy/maven2/") {
              "authentication"("userName" to ossrhUsername, "password" to ossrhPassword)
            }

            "snapshotRepository"("url" to "https://oss.sonatype.org/content/repositories/snapshots/") {
              "authentication"("userName" to ossrhUsername, "password" to ossrhPassword)
            }
          }

          pom {
            packaging = "jar"
            name = project.name
            description = project.description.orEmpty()

            withGroovyBuilder {
              "project" {
                "url" to "https://libktx.github.io/"

                "licenses" {
                  "license"(
                    "name" to "CC0-1.0",
                    "url" to "https://creativecommons.org/publicdomain/zero/1.0/"
                  )
                }

                "scm" {
                  "connection"("scm:git:git@github.com:libktx/ktx.git")
                  "developerConnection"("scm:git:git@github.com:libktx/ktx.git")
                  "url"("https://github.com/libktx/ktx/")
                }

                "developers" {
                  "developer" {
                    "id"("mj")
                    "name"("MJ")
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

nexusStaging {
  packageGroup = libGroup
  username = ossrhUsername
  password = ossrhPassword
}

distributions {
  main {
    baseName = libVersion
    contents {
      project.subprojects.forEach { sub ->
        into("lib").from(sub.tasks["jar"])
        into("src").from(sub.tasks["sourcesJar"])
        into("doc").from(sub.tasks["dokkaZip"])
      }
    }
  }
}

val generateDocumentationIndex by tasks.registering {
  doLast {
    val indexFile = file("$buildDir/dokka/index.html")
    delete(indexFile)
    indexFile.appendText("""
      <html>
      <head>
        <meta charset="utf-8">
        <title>KTX Sources Documentation</title>
        <link rel="stylesheet" href="style.css">
      </head>
      <body>
      <ul>
        <h1>KTX Documentation</h1>
        <p>This page contains documentation generated via Dokka from KTX sources.</p>
        <p>To see the official KTX website, follow <a href="https://libktx.github.io/">this link</a>.</p>
        <h2>Modules</h2>
      ${subprojects.joinToString("\n") { "  <li><a href=\"${it.name}/\">ktx-${it.name}</a></li>" }}
      </ul>
      </body>
      </html>
      """.trimIndent())
  }
}

tasks.register<Copy>("gatherDokkaDocumentation") {
  subprojects.forEach { subproject ->
    from(subproject.buildDir)
    include("dokka/${subproject.name}/**")
    include("dokka/style.css")
  }

  into(buildDir)
  finalizedBy(generateDocumentationIndex)
}

tasks.register<JavaExec>("linterIdeSetup") {
  description = "Apply Kotlin code style changes to IntelliJ formatter."
  main = "com.pinterest.ktlint.Main"
  classpath = configurations["linter"]
  args = listOf("applyToIDEAProject", "-y")
}
