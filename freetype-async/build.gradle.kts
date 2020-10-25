val kotlinCoroutinesVersion: String by rootProject
val gdxVersion: String by rootProject

dependencies {
  implementation(project(":assets-async"))
  implementation(project(":freetype"))
  compileOnly("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")

  // testImplementation(project(":async").sourceSets.test.output)
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
  testImplementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
  testImplementation("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
