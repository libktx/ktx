val kotlinCoroutinesVersion: String by rootProject
val gdxVersion: String by rootProject

val async = project(":async")
val asyncSourceSets = async

dependencies {

  "compile"(project(":assets"))
  "compile"(async)
  // "testCompile"(async.sourceSets.test.output)
  "testCompile"("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
