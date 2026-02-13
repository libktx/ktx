import ktx.*

dependencies {
  api(project(":scene2d"))
  api("com.github.tommyettinger:textratypist:$textraTypistVersion")

  testImplementation(
    project(":scene2d")
      .dependencyProject.sourceSets.test
      .get()
      .output,
  )
  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
  // Includes Scene2D skin used in tests:
  testImplementation("com.kotcrab.vis:vis-ui:$visUiVersion")
}
