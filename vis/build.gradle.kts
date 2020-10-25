val visUiVersion: String by rootProject
val gdxVersion: String by rootProject

dependencies {
  api(project(":scene2d"))
  api("com.kotcrab.vis:vis-ui:$visUiVersion")

  testImplementation(project(":scene2d").dependencyProject.sourceSets.test.get().output)
  testImplementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
  testImplementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
}
