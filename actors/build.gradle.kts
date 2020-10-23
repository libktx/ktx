val kotlinVersion: String by rootProject
val gdxVersion: String by rootProject

dependencies {
  "compileOnly"("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
