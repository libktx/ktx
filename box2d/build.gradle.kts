val gdxVersion: String by rootProject

dependencies {
  compileOnly("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop")
}
