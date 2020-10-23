val visUiVersion: String by rootProject

dependencies {
  "compile"(project(":style"))
  "compile"("com.kotcrab.vis:vis-ui:$visUiVersion")
}
