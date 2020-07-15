@file:Suppress("unused")

package ktx.tools

import org.gradle.api.Plugin
import org.gradle.api.Project

private const val EXTENSION_NAME = "ktxTools"

/**
 * The Gradle plugin that sets up all tasks of ktx-tools.
 */
class KtxToolsPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val ktxToolsExtension = project.extensions.create(EXTENSION_NAME, KtxToolsPluginExtension::class.java)

    project.tasks.create("createBundleLines") { task ->
      task.doLast {
        with (ktxToolsExtension.createBundleLines) {
          BundleLinesCreator.execute(
            targetPackage = requireNotNull(targetPackage) {
              "Cannot create BundleLines if target package is not set. This can be set in the gradle build file, e.g.:" +
                "\n    $EXTENSION_NAME.${KtxToolsPluginExtension::createBundleLines.name}" +
                ".${CreateBundleLinesParams::targetPackage.name} = \"com.mycompany.mygame\"\n"
            },
            explicitBundlesDirectory = bundlesDirectory,
            searchSubdirectories = searchSubDirectories,
            targetSourceDirectory = targetSourceDirectory,
            enumClassName = enumClassName,
            codeIndent = codeIndent
          )
        }
      }
      task.group = "ktx tools"
    }
  }
}

/**
 * Container for all ktx-tools task parameters.
 */
open class KtxToolsPluginExtension {
  /**
   * Parameters for the `createBundleLines` task.
   * */
  var createBundleLines = CreateBundleLinesParams()
}

/**
 * Parameters for the `createBundleLines` task.
 * */
open class CreateBundleLinesParams {
  /**
   * The package created enums will be placed in. If null (the default), the `createBundleLines` task cannot be used.
   * */
  var targetPackage: String? = null

  /** Directory that is searched for properties files. If null (the default), the first non-null and existing directory
   * in descending precedence of `android/assets/i18n`, `android/assets/nls`, `android/assets`, `core/assets/i18n`,
   * `core/assets/nls`, and `core/assets` is used.
   * */
  var bundlesDirectory: String? = null

  /**
   * Whether to search subdirectories of the parent directory. Default true.
   * */
  var searchSubDirectories: Boolean = true

  /**
   * The directory enum source files will be placed in. Default `"core/src"`.
   * */
  var targetSourceDirectory: String = "core/src"

  /**
   * The name of the generated enum class. If non-null, a single enum class is created for all bundles found. If null,
   * each unique base bundle's name will be used for a distinct enum class. Default `"Nls"`.
   * */
  var enumClassName: String? = "Nls"

  /**
   * The whitespace String used as an indent in the generated code. Default four space characters.
   */
  var codeIndent: String = "    "
}
