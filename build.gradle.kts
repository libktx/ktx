import ktx.*
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    jcenter()
    mavenCentral()
  }

  val kotlinVersion: String by project
  val junitPlatformVersion: String by project

  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    classpath("org.junit.platform:junit-platform-gradle-plugin:$junitPlatformVersion")
  }
}

val libGroup: String by project
val ossrhUsername: String by project
val ossrhPassword: String by project

plugins {
  java
  distribution
  id("org.jetbrains.dokka") version "1.4.10.2"
  id("io.codearte.nexus-staging") version "0.22.0"
}

repositories {
  jcenter()
}

val libVersion = file("version.txt").readText().trim()

allprojects {
  val linter = configurations.create("linter")

  dependencies {
    linter("com.pinterest:ktlint:$ktlintVersion")
  }
}

subprojects {
  apply(plugin = "maven-publish")
  apply(plugin = "java")
  apply(plugin = "kotlin")
  apply(plugin = "signing")
  apply(plugin = "jacoco")

  val isReleaseVersion = !libVersion.endsWith("SNAPSHOT")

  repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
  }

  configurations {
    testImplementation.get().extendsFrom(compileOnly.get())
  }

  group = libGroup
  version = libVersion
  val projectName: String by project
  val projectDesc: String by project

  base {
    archivesBaseName = projectName
  }

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

  dependencies {
    val kotlinVersion: String by project

    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
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
      events = setOf(FAILED, SKIPPED, STANDARD_OUT)
      exceptionFormat = FULL
      showExceptions = true
      showCauses = true
      showStackTraces = true

      debug {
        events = setOf(STARTED, FAILED, PASSED, SKIPPED, STANDARD_ERROR, STANDARD_OUT)
        exceptionFormat = FULL
      }

      info.events = debug.events
      info.exceptionFormat = debug.exceptionFormat
    }
  }

  tasks.named<Jar>("jar") {
    from(sourceSets.main.get().output)
    archiveBaseName.set(projectName)
  }

  val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
  }

  afterEvaluate {
    val sourcesJar by tasks.registering(Jar::class) {
      from(sourceSets.main.get().allSource)
      archiveClassifier.set("sources")
    }

    artifacts {
      archives(javadocJar)
      archives(sourcesJar)
    }

    afterEvaluate {
      rootProject.distributions {
        main {
          distributionBaseName.set(libVersion)
          contents {
            into("lib").from(tasks.jar)
            into("src").from(tasks["sourcesJar"])
          }
        }
      }
    }
  }

  tasks.withType<Sign> { onlyIf { isReleaseVersion } }

  configure<SigningExtension> {
    setRequired { isReleaseVersion && gradle.taskGraph.hasTask("uploadArchives") }
    sign(configurations.archives.get())
  }

  tasks.register("uploadSnapshot") {
    if (!isReleaseVersion) finalizedBy(tasks["publishAllPublicationsToMavenRepository"])
  }

  configure<PublishingExtension> {
    repositories {
      maven {
        val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
        val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

        credentials {
          username = ossrhUsername
          password = ossrhPassword
        }
      }
    }

    publications {
      create<MavenPublication>("mavenKtx") {
        pom {
          name.set(projectName)
          artifactId = tasks.jar.get().archiveBaseName.get()
          packaging = "jar"
          description.set(projectDesc)
          from(components["kotlin"])

          url.set("https://libktx.github.io/")

          licenses {
            license {
              name.set("CC0-1.0")
              url.set("https://creativecommons.org/publicdomain/zero/1.0/")
            }
          }

          scm {
            connection.set("scm:git:git@github.com:libktx/ktx.git")
            developerConnection.set("scm:git:git@github.com:libktx/ktx.git")
            url.set("https://github.com/libktx/ktx/")
          }

          developers {
            developer {
              id.set("mj")
              name.set("MJ")
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

tasks.register<JavaExec>("linterIdeSetup") {
  description = "Apply Kotlin code style changes to IntelliJ formatter."
  main = "com.pinterest.ktlint.Main"
  classpath = configurations["linter"]
  args = listOf("applyToIDEAProject", "-y")
}
