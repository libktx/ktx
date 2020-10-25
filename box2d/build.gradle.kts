val gdxVersion: String by rootProject

dependencies {
  provided("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-desktop")
}
