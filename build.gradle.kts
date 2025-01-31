import ktx.*
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }

  val dokkaVersion: String by project
  val kotlinVersion: String by project
  val junitPlatformVersion: String by project

  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    classpath("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
    classpath("org.junit.platform:junit-platform-gradle-plugin:$junitPlatformVersion")
  }
}

val libGroup: String by project
val ossrhUsername: String by project
val ossrhPassword: String by project

plugins {
  java
  distribution
  id("io.codearte.nexus-staging") version "0.22.0"
}

repositories {
  mavenCentral()
}

val libVersion = file("version.txt").readText().trim()

allprojects {
  apply(plugin = "org.jetbrains.dokka")

  val linter = configurations.create("linter")

  dependencies {
    linter("com.pinterest.ktlint:ktlint-cli:$ktlintVersion")
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
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
  }

  group = libGroup
  version = libVersion
  val projectName: String by project
  val projectDesc: String by project

  base {
    archivesName.set(projectName)
  }

  java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_1_8.toString()
      freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }
    jvmTargetValidationMode.set(JvmTargetValidationMode.IGNORE)
  }
  val compileTestKotlin: KotlinCompile by tasks
  compileTestKotlin.kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()

  dependencies {
    val kotlinVersion: String by project

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("io.kotlintest:kotlintest:$kotlinTestVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$kotlinMockitoVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
  }

  tasks.register("lint", JavaExec::class) {
    mainClass.set("com.pinterest.ktlint.Main")
    description = "Check Kotlin code style."
    group = "verification"
    classpath = configurations["linter"]
    args = listOf("src/**/*.kt")

    tasks["check"].dependsOn(this)
  }

  tasks.register("format", JavaExec::class) {
    mainClass.set("com.pinterest.ktlint.Main")
    description = "Fix Kotlin code style."
    group = "formatting"
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

  val dokkaHtml by tasks.getting

  tasks.register<Zip>("dokkaZip") {
    from("$buildDir/dokka/html")
    dependsOn(dokkaHtml)
  }

  val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from("$buildDir/dokka/html")
    dependsOn(dokkaHtml)
  }

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
          into("lib") {
            from(tasks.jar)
          }
          into("doc") {
            from(tasks["dokkaZip"])
          }
          into("src") {
            from(tasks["sourcesJar"])
          }
        }
      }
    }
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
          packaging = "jar"
          description.set(projectDesc)
          afterEvaluate {
            artifactId = tasks.jar.get().archiveBaseName.get()
          }

          from(components["kotlin"])
          artifact(sourcesJar)
          artifact(javadocJar)

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

  tasks.withType<Sign> { onlyIf { isReleaseVersion } }

  configure<SigningExtension> {
    setRequired { isReleaseVersion && gradle.taskGraph.hasTask("publish") }
    sign(extensions.getByType<PublishingExtension>().publications["mavenKtx"])
  }
}

nexusStaging {
  packageGroup = libGroup
  username = ossrhUsername
  password = ossrhPassword
}

tasks.register<JavaExec>("linterIdeSetup") {
  mainClass.set("com.pinterest.ktlint.Main")
  description = "Apply Kotlin code style changes to IntelliJ formatter."
  classpath = configurations["linter"]
  args = listOf("applyToIDEAProject")
}
