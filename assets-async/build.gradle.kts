val kotlinCoroutinesVersion: String by rootProject
val gdxVersion: String by rootProject
val async = project(":async")

dependencies {

  api(project(":assets"))
  api(async)

  testImplementation(async.sourceSets.test.get().output)
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
  testImplementation("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
