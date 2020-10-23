import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinCoroutinesVersion: String by rootProject
val kotlinVersion: String by rootProject
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
  "compileOnly"("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
  "compileOnly"("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
  "testCompile"("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
  "testCompile"("me.alexpanov:free-port-finder:1.0") // Version unlikely to change, not parametrized.
  "testCompile"("com.github.tomakehurst:wiremock:$wireMockVersion")
  "testCompile"("org.slf4j:slf4j-nop:$slf4jVersion")
}
