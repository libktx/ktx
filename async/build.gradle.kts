import ktx.*

dependencies {
  api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

  testImplementation("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
  testImplementation("me.alexpanov:free-port-finder:1.0") // Version unlikely to change, not parametrized.
  testImplementation("com.github.tomakehurst:wiremock:$wireMockVersion")
  testImplementation("org.slf4j:slf4j-nop:$slf4jVersion")
}
