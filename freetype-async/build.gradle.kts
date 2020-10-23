val kotlinCoroutinesVersion: String by rootProject
val kotlinVersion: String by rootProject
val gdxVersion: String by rootProject

dependencies {
  "compile"(project(":assets-async"))
  "compile"(project(":freetype"))
  "compileOnly"("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
  "compileOnly"("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")

  // "testCompile"(project(":async").sourceSets.test.output)
  "testCompile"("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
  "testCompile"("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
