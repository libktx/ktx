import ktx.*

dependencies {
  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
  // Includes Scene2D skin used in tests:
  testImplementation("com.kotcrab.vis:vis-ui:$visUiVersion")
}
