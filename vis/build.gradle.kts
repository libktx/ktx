val kotlinVersion: String by rootProject
val visUiVersion: String by rootProject
val gdxVersion: String by rootProject

dependencies {
  implementation(project(":scene2d"))
  compileOnly("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
  implementation("com.kotcrab.vis:vis-ui:$visUiVersion")
  // testCompile project(":scene2d").sourceSets.test.output
  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
