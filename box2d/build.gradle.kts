val kotlinVersion: String by rootProject
val gdxVersion: String by rootProject

dependencies {
  "compileOnly"("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
  "compileOnly"("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop")
}
