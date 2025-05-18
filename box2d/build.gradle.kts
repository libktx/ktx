import ktx.gdxVersion

dependencies {
  api("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
