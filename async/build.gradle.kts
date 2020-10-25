import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinCoroutinesVersion: String by rootProject
val wireMockVersion: String by rootProject
val gdxVersion: String by rootProject
val slf4jVersion: String by rootProject

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs += listOf(
      "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
      "-Xuse-experimental=kotlinx.coroutines.InternalCoroutinesApi"
    )
  }
}

dependencies {
  api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
  testImplementation("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
  testImplementation("me.alexpanov:free-port-finder:1.0") // Version unlikely to change, not parametrized.
  testImplementation("com.github.tomakehurst:wiremock:$wireMockVersion")
  testImplementation("org.slf4j:slf4j-nop:$slf4jVersion")
}
