import ktx.*

dependencies {
  api(kotlin("scripting-jsr223"))

  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
