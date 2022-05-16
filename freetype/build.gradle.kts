import ktx.*

dependencies {
  api(project(":assets"))
  api("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")

  testImplementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
