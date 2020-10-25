val kotlinCoroutinesVersion: String by rootProject
val gdxVersion: String by rootProject

dependencies {
  api(project(":assets-async"))
  api(project(":freetype"))
  provided("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")

  testImplementation(project(":async").dependencyProject.sourceSets.test.get().output)
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
  testImplementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
  testImplementation("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
