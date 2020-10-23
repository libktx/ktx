val kotlinVersion: String by rootProject
val gdxVersion: String by rootProject

dependencies {
  "compile"(project(":assets"))
  "compileOnly"("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
  "compileOnly"("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
  "testCompile"("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
