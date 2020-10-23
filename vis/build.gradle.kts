val kotlinVersion: String by rootProject
val visUiVersion: String by rootProject
val gdxVersion: String by rootProject

dependencies {
  "compile"(project(":scene2d"))
  "compileOnly"("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
  "compile"("com.kotcrab.vis:vis-ui:$visUiVersion")
  // testCompile project(":scene2d").sourceSets.test.output
  "testCompile"("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  "testCompile"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
