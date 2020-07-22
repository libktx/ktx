@file:Suppress("unused")

package ktx.tools

import org.gradle.api.Plugin
import org.gradle.api.Project

internal const val TASK_GROUP = "ktx"
internal const val PROJECT_EXTENSION = "ktx"
internal const val BUNDLE_LINES_CREATOR_TASK = "createBundleLines"

/**
 * The Gradle plugin that sets up all tasks of ktx-tools, placing them in the `ktx` group.
 */
class KtxToolsPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val ktxToolsExtension = project.extensions.create(PROJECT_EXTENSION, KtxToolsPluginExtension::class.java)
    project.tasks.create(BUNDLE_LINES_CREATOR_TASK) { task ->
      BundleLinesCreator.logger = project.logger
      task.doLast { BundleLinesCreator.execute(ktxToolsExtension.createBundleLines) }
      task.group = TASK_GROUP
    }
  }
}

/**
 * Container for all `ktx` task parameters.
 */
open class KtxToolsPluginExtension {
  /**
   * Parameters for the `createBundleLines` task.
   * */
  var createBundleLines = BundleLinesCreatorParams()
}
