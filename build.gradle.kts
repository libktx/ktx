import ktx.*
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

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
val mavenPublishingPluginVersion: String by project

plugins {
  java
  distribution
  id("com.vanniktech.maven.publish") version "0.33.0"
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
  apply(plugin = "java")
  apply(plugin = "kotlin")
  apply(plugin = "signing")
  apply(plugin = "jacoco")
  apply(plugin = "com.vanniktech.maven.publish.base")

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
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_1_8)
      freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
    jvmTargetValidationMode.set(JvmTargetValidationMode.IGNORE)
  }
  val compileTestKotlin: KotlinCompile by tasks
  compileTestKotlin.compilerOptions.jvmTarget.set(JvmTarget.JVM_11)

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
    from("${layout.buildDirectory}/dokka/html")
    dependsOn(dokkaHtml)
  }

  mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    if (isReleaseVersion) {
      signAllPublications()
    }
    configure(KotlinJvm(
      javadocJar = if (isReleaseVersion) JavadocJar.Dokka("dokkaHtml") else JavadocJar.None(),
      sourcesJar = true
    ))

    coordinates(libGroup, projectName, libVersion)

    pom {
      name.set(projectName)
      description.set(projectDesc)
      inceptionYear.set("2016")
      url.set("https://github.com/libktx/ktx")
      licenses {
        license {
          name.set("CC0-1.0")
          url.set("https://creativecommons.org/publicdomain/zero/1.0/")
        }
      }
      developers {
        developer {
          id.set("mj")
          name.set("MJ")
          url.set("https://github.com/czyzby")
        }
      }
      scm {
        url.set("https://github.com/libktx/ktx")
        connection.set("scm:git:git://github.com/libktx/ktx.git")
        developerConnection.set("scm:git:ssh://git@github.com/libktx/ktx.git")
      }
    }
  }

  tasks.register("uploadSnapshot") {
    if (!isReleaseVersion) finalizedBy(tasks["publishToMavenCentral"])
  }

  tasks.withType<Sign> { onlyIf { isReleaseVersion } }
}

tasks.register<JavaExec>("linterIdeSetup") {
  mainClass.set("com.pinterest.ktlint.Main")
  description = "Apply Kotlin code style changes to IntelliJ formatter."
  classpath = configurations["linter"]
  args = listOf("applyToIDEAProject")
}
