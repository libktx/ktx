val kotlinCoroutinesVersion: String by rootProject
val gdxVersion: String by rootProject

dependencies {
  api(project(":assets"))
  api(project(":async"))
  // testImplementation(async.sourceSets.test.output)
  api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
  testImplementation("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
