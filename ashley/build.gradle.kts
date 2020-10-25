val ashleyVersion: String by rootProject
val spekVersion: String by rootProject
val assertjVersion: String by rootProject
val junitPlatformVersion: String by rootProject

dependencies {
  provided("com.badlogicgames.ashley:ashley:$ashleyVersion")

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
