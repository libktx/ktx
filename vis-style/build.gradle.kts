val visUiVersion: String by rootProject

dependencies {
  api(project(":style"))
  api("com.kotcrab.vis:vis-ui:$visUiVersion")
}
