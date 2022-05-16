import ktx.*

val junitPlatformVersion: String by project

dependencies {
  api("com.badlogicgames.ashley:ashley:$ashleyVersion")

  testImplementation("org.jetbrains.spek:spek-api:$spekVersion")
  testImplementation("org.assertj:assertj-core:$assertjVersion")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
  testRuntimeOnly("org.jetbrains.spek:spek-junit-platform-engine:$spekVersion")
}

tasks.withType<Test> {
  testLogging {
    showExceptions = true
    events("FAILED", "SKIPPED")
  }

  useJUnitPlatform {
    includeEngines("spek")
  }
}
