val kotlinCoroutinesVersion: String by rootProject
val gdxVersion: String by rootProject

dependencies {
  val async = project(":async").dependencyProject

  api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
  api(project(":assets"))
  api(async)

  testImplementation(async.sourceSets.test.get().output)
  testImplementation("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
