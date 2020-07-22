package ktx.tools

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties
import kotlin.streams.toList
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Searches for properties files and generates Ktx BundleLine class files for them when executed. The companion object
 * instance can be used directly or behavior can be customized by subclassing.
 * */
open class BundleLinesCreator {

  companion object Default : BundleLinesCreator()

  /**
   * The logging implementation used when executed.
   * */
  var logger: Logger = LoggerFactory.getLogger(this::class.java)

  internal fun execute(params: BundleLinesCreatorParams) {
    with(params) {
      execute(
        targetPackage = requireNotNull(targetPackage) {
          "Cannot create BundleLines if target package is not set. This can be set in the gradle build file, e.g.:" +
            "\n    $PROJECT_EXTENSION.${KtxToolsPluginExtension::createBundleLines.name}" +
            ".${BundleLinesCreatorParams::targetPackage.name} = \"com.mycompany.mygame\"\n"
        },
        explicitBundlesDirectory = bundlesDirectory,
        searchSubdirectories = searchSubDirectories,
        targetSourceDirectory = targetSourceDirectory,
        enumClassName = enumClassName,
        codeIndent = codeIndent
      )
    }
  }

  /**
   * Executes creation of enum source files.
   * @param targetPackage The package of the generated enums.
   * @param explicitBundlesDirectory Path directory that is searched for properties file, relative to [pathPrefix]. If
   * null (the default), a directory is searched for as described by [findFallbackAssetDirectory].
   * @param searchSubdirectories Whether to search subdirectories of the parent directory. Default true.
   * @param targetSourceDirectory The directory enum source files will be placed in, relative to [pathPrefix]. Default
   * `"core/src"`.
   * @param enumClassName The name of the generated enum class. If non-null, a single enum class is created for all
   * bundles found. If null, each unique base bundle's name will be used for a distinct enum class. Default `"Nls"`.
   * @param codeIndent The whitespace String used as an indent in the generated code. Default four space characters.
   * @param pathPrefix A prefix applied for all file operations. Default is empty, so the working directory is the
   * parent.
   */
  fun execute(
    targetPackage: String,
    explicitBundlesDirectory: String? = null,
    searchSubdirectories: Boolean = true,
    targetSourceDirectory: String = "core${File.separator}src",
    enumClassName: String? = "Nls",
    codeIndent: String = "    ",
    pathPrefix: String = ""
  ) {
    require(codeIndent.all(Char::isWhitespace)) { "codeIntent must consist entirely of whitespace." }
    val parentDirectory = explicitBundlesDirectory?.let { File(pathPrefix + it) }
      ?: findFallbackAssetDirectory(pathPrefix)
    requireNotNull(parentDirectory) { "Failed to find asset directory." }
    val baseFiles = findBasePropertiesFiles(parentDirectory, searchSubdirectories)
    val enumNamesToEntryNames = collectEnums(baseFiles, enumClassName)
    val outDir = File(
      "$pathPrefix$targetSourceDirectory${File.separator}${targetPackage.replace(".", File.separator)}"
    ).apply { mkdirs() }
    for ((enumName, entryNames) in enumNamesToEntryNames) {
      val outFile = File(outDir, "$enumName.kt")
      val sourceCode = generateKtFileContent(targetPackage, enumName, entryNames, codeIndent)
      outFile.writeText(sourceCode)
    }
    logger.info(
      "Created BundleLine enum class(es) for bundles in directory \"$parentDirectory\":\n" +
        enumNamesToEntryNames.keys.joinToString(separator = ",\n") { "  $it" } +
        "\nin package $targetPackage in source directory \"$pathPrefix$targetSourceDirectory\"."
    )
  }

  /**
   * Finds the assets directory to be searched for properties files if no explicit directory was provided. The default
   * implementation finds the first existing directory in descending precedence of `android/assets/i18n`,
   * `android/assets/nls`, `android/assets`, `core/assets/i18n`, `core/assets/nls`, and `core/assets`.
   * @param pathPrefix A prefix that should be applied to all paths.
   * @return The parent directory, or null if none exists.
   */
  protected open fun findFallbackAssetDirectory(pathPrefix: String): File? {
    return listOf(
      "android/assets/i18n",
      "android/assets/nls",
      "android/assets/",
      "core/assets/i18n",
      "core/assets/nls",
      "core/assets"
    )
      .map { File(pathPrefix + it) }
      .firstOrNull(File::isDirectory)
  }

  /**
   * Collects properties files that will be processed. The default implementation finds all files with name suffix
   * `.properties` and no underscore in the name.
   * @param parentDirectory The directory that will be searched.
   * @param searchSubdirectories Whether to include sub-directories of the parent directory in the search.
   * @return A list of all applicable properties files found.
   */
  protected open fun findBasePropertiesFiles(
    parentDirectory: File,
    searchSubdirectories: Boolean
  ): Collection<File> {
    return Files.walk(parentDirectory.absoluteFile.toPath(), if (searchSubdirectories) Int.MAX_VALUE else 1)
      .filter { path ->
        path.fileName.toString().let { fileName ->
          fileName.endsWith(".properties") && '_' !in fileName
        }
      }
      .map(Path::toFile)
      .toList()
  }

  /**
   * Takes the target [propertiesFiles] and returns data for building enums from them. By default, if [commonEnumName]
   * is non-null, a single base name is output and all property keys will be merged into the single set. Otherwise, the
   * properties files' names are used as base names.
   * @param propertiesFiles All properties files that should be included
   * @param commonEnumName A name that should be used for a single enum that covers all input properties, or null to
   * indicate that each properties file should have its own base name.
   * @return A map of enum names to the enum entry names that should be members of those enums.
   * */
  protected open fun collectEnums(
    propertiesFiles: Collection<File>,
    commonEnumName: String?
  ): Map<String, Set<String>> {
    val outMap = mutableMapOf<String, MutableSet<String>>()
    val sanitizedCommonBaseName = commonEnumName?.let { convertToEnumName(it) }
    if (sanitizedCommonBaseName != commonEnumName)
      logger.warn("The provided enumClassName $commonEnumName was changed to $sanitizedCommonBaseName.")
    for (file in propertiesFiles) {
      val enumName = sanitizedCommonBaseName ?: convertToEnumName(file.nameWithoutExtension)
      val enumNames = Properties().run {
        load(file.inputStream())
        stringPropertyNames().mapNotNull { convertToEntryName(it) }
      }
      outMap.getOrPut(enumName, ::mutableSetOf)
        .addAll(enumNames)
    }
    return outMap
  }

  /**
   * Converts the name of a properties file (without suffix) into an appropriate name for a Kotlin enum class. The
   * default behavior is to trim leading invalid characters (anything besides letters and underscores) and remove
   * remaining whitespace by converting to PascalCase.
   * @param name The input name, retrieved from a `.properties` file's name, or the `enumClassName` parameter of
   * [execute].
   * @return A name based on [name] that can be used as an enum class name.
   * */
  protected open fun convertToEnumName(name: String): String {
    return name.trimStart { !it.isLetter() && it != '_' }
      .split("\\s+".toRegex())
      .joinToString("", transform = String::capitalize)
      .also { require(it.isNotEmpty()) { "File name $name cannot be automatically converted to a valid enum name." } }
  }

  /**
   * Converts the name of a properties entry into a valid enum entry name.
   * @param name The input name, retrieved from a properties file.
   * @return A name based on [name] that is a valid enum entry name, or null if the input name cannot be used as a valid
   * enum entry.
   * */
  protected open fun convertToEntryName(name: String): String? {
    if (name.isEmpty()) {
      logger.warn("A blank property name was encountered and will be omitted.")
      return null
    }
    if ('`' in name) {
      logger.warn("The property name '$name' cannot be converted into a usable enum entry and will be omitted.")
      return null
    }
    if (name[0].isDigit() || !name.all { it.isLetterOrDigit() || it == '_' })
      return "`$name`"
    return name
  }

  /**
   * Generates a String representing the complete file content of a .kt file containing a `BundleLine` enum class. The
   * default implementation also adds suppression of improper enum entry names and a companion object property that can
   * be used to enable the `BundleLine` `nls` and `invoke` functions.
   * @param packageName The package the enum class should be a part of.
   * @param enumClassName The class name for the enum.
   * @param entryNames The names of all the enum's entries.
   * @param indent The whitespace String used to indent code.
   * @return Source code for a complete .kt file.
   * */
  protected open fun generateKtFileContent(
    packageName: String,
    enumClassName: String,
    entryNames: Set<String>,
    indent: String
  ): String {
    return buildString {
      append("package $packageName\n\n")
      append("import ktx.i18n.BundleLine\n")
      append("import com.badlogic.gdx.utils.I18NBundle\n\n")
      append("@Suppress(\"EnumEntryName\")\n")
      append("enum class $enumClassName : BundleLine {\n")
      for ((index, key) in entryNames.withIndex()) {
        val lineEnding = if (index < entryNames.size - 1) "," else ";\n"
        append("${indent}$key$lineEnding\n")
      }
      append("${indent}override val bundle: I18NBundle\n")
      append("${indent}${indent}get() = i18nBundle\n\n")
      append("${indent}companion object {\n")
      append("${indent}$indent/**\n")
      append("${indent}$indent * The bundle used for [BundleLine.nls] and [BundleLine.invoke] for this enum's values. Must\n")
      append("${indent}$indent * be set explicitly before extracting the translated texts.\n")
      append("${indent}$indent * */\n")
      append("${indent}${indent}lateinit var i18nBundle: I18NBundle\n")
      append("$indent}\n")
      append('}')
    }
  }
}

/**
 * Parameters for running [BundleLinesCreator], intended for the `createBundleLines` Gradle task.
 * */
open class BundleLinesCreatorParams {
  /**
   * The package created enums will be placed in. Must be set, or the `createBundleLines` task cannot be used.
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
  var targetSourceDirectory: String = "core${File.separator}src"

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
