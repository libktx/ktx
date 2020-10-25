val visUiVersion: String by rootProject
val gdxVersion: String by rootProject

dependencies {
  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
  // Includes Scene2D skin used in tests:
  testImplementation("com.kotcrab.vis:vis-ui:$visUiVersion")
}
