package ktx.tools

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.AbstractFileFilter
import org.apache.commons.io.filefilter.FileFilterUtils
import org.apache.commons.io.filefilter.SuffixFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File
import java.io.IOException
import java.util.Properties

/** Searches for properties files and generates Ktx BundleLine class files for them when executed. The companion object
 * instance can be used directly or behavior can be customized by subclassing. */
open class BundleLinesCreator {

  companion object : BundleLinesCreator()

  /**
   * Executes creation of enum source files.
   * @param targetPackage The package created enums will be placed in.
   * @param explicitParentDirectory Directory that is searched for properties files when creating BundleLines. If null
   * (the default), a directory is searched for as described by [findParentDirectory].
   * @param searchSubdirectories Whether to search subdirectories of the parent directory. Default true.
   * @param targetSourceDirectory The directory enum source files will be placed in. Default `"core/src"`.
   * @param enumClassName The name of the generated enum class. If non-null, a single enum class is created for all
   * bundles found. If null, each unique base bundle's name will be used for a distinct enum class. Default `"Nls"`.
   */
  fun execute(
    targetPackage: String,
    explicitParentDirectory: String? = null,
    searchSubdirectories: Boolean = true,
    targetSourceDirectory: String = "core/src",
    enumClassName: String? = "Nls"
  ) {
    try {
      val parentDirectory = findParentDirectory(explicitParentDirectory)
      if (parentDirectory == null) {
        printlnRed("Failed to find asset directory. No files will be created.")
        return
      }
      val baseFiles = findBasePropertiesFiles(parentDirectory, searchSubdirectories)
      val enumNamesToEntryNames = collectKeys(baseFiles, enumClassName)
      val outDir = File("$targetSourceDirectory/${targetPackage.replace(".", "/")}")
        .apply { mkdirs() }
      for ((enumName, entryNames) in enumNamesToEntryNames) {
        val outFile = File(outDir, "$enumName.kt")
        val sourceCode = generateKtFileContent(targetPackage, enumName, entryNames)
        outFile.writeText(sourceCode)
      }
      println("Created BundleLine enum class(es) for bundles in directory $parentDirectory:")
      println(enumNamesToEntryNames.keys.joinToString(separator = ",\n", prefix = "  "))
      println("in package $targetPackage in source directory $targetSourceDirectory.")
    } catch (e: IOException) {
      printlnRed("An IO error was encountered while executing BundleLinesCreator.")
      throw e
    }
  }

  /** Finds the parent directory that will be searched for properties files. The default implementation finds the first
   * non-null and existing directory in descending precedence of [assetDirectory], `android/assets/i18n`,
   * `android/assets/nls`, `android/assets`, `core/assets/i18n`, `core/assets/nls`, and `core/assets`.
   * @return The parent directory, or null if none exists.
   */
  protected open fun findParentDirectory(assetDirectory: String?): File? {
    return listOfNotNull(
      assetDirectory,
      "android/assets/i18n",
      "android/assets/nls",
      "android/assets/",
      "core/assets/i18n",
      "core/assets/nls",
      "core/assets"
    )
      .map(::File).firstOrNull(File::isDirectory)
  }

  /** Collects properties files that will be processed. The default implementation finds all files with name suffix
   * `.properties` and no underscore in the name. The [searchSubdirectories]
   * property determines whether subdirectories of [parentDirectory] are also included in the search.
   * @param parentDirectory The directory that will be searched.
   * @return A list of all applicable properties files found.
   */
  protected open fun findBasePropertiesFiles(parentDirectory: File, searchSubdirectories: Boolean): Collection<File> {
    val noUnderscoresFilter = object : AbstractFileFilter() {
      override fun accept(dir: File, name: String) = '_' !in name
    }

    return FileUtils.listFiles(
      parentDirectory,
      FileFilterUtils.and(SuffixFileFilter("properties"), noUnderscoresFilter),
      if (searchSubdirectories) TrueFileFilter.INSTANCE else null
    )
  }

  /** Takes the target [propertiesFiles] and returns a map of output enum class BundleLines to their set of String keys.
   * By default, if [commonBaseName] is non-null, a single base name is output and all property keys will be merged
   * into the single set. Otherwise, the properties files' names are used as base names. */
  protected open fun collectKeys(propertiesFiles: Collection<File>, commonBaseName: String?): Map<String, Set<String>> {
    val outMap = mutableMapOf<String, MutableSet<String>>()
    val sanitizedCommonBaseName = commonBaseName?.let { sanitizeEnumName(it) }
    if (sanitizedCommonBaseName != commonBaseName)
      println("The provided enumClassName $commonBaseName was changed to $sanitizedCommonBaseName.")
    for (file in propertiesFiles) {
      val enumName = sanitizedCommonBaseName ?: sanitizeEnumName(file.nameWithoutExtension)
      val propertyNames = Properties().run {
        load(file.inputStream())
        stringPropertyNames()
      }
      val enumNames = propertyNames.mapNotNull { sanitizeEntryName(it) }
      if (propertyNames.size > enumNames.size)
        printlnRed("Warning: Properties file ${file.name} contains at least one empty key, which will be omitted.")
      outMap.getOrPut(enumName, ::mutableSetOf)
        .addAll(enumNames)
    }
    return outMap
  }

  private fun sanitizeEnumName(name: String): String {
    return name.trimStart { !it.isLetterOrDigit() && it != '_' }
      .split("\\s+".toRegex())
      .joinToString("", transform = String::capitalize)
  }

  /** @return A name based on [name] that is a valid enum entry name, or null if the input name is empty. */
  private fun sanitizeEntryName(name: String): String? {
    if (name.isEmpty())
      return null
    if (name[0].isDigit() || !name.all { it.isLetterOrDigit() || it == '_' })
      return "`$name`"
    return name
  }

  protected open fun generateKtFileContent(
    packageName: String,
    enumClassName: String,
    entryNames: Set<String>
  ): String {
    return buildString {
      append("@file:Suppress(\"EnumEntryName\")\n\n")
      append("package $packageName\n\n")
      append("import ktx.i18n.BundleLine\n")
      append("import com.badlogic.gdx.utils.I18NBundle\n")
      append("\nenum class $enumClassName: BundleLine {\n")
      for ((index, key) in entryNames.withIndex()) {
        val lineEnding = if (index < entryNames.size - 1) ',' else ';'
        append("${IND}$key$lineEnding\n")
      }
      append("\n${IND}override val bundle: I18NBundle\n")
      append("${IND2}get() = i18nBundle\n")
      append("\n${IND}companion object {\n")
      append("${IND2}/** The bundle used for [BundleLine.nls] and [BundleLine.invoke] for this enum's values. */\n")
      append("${IND2}lateinit var i18nBundle: I18NBundle\n")
      append("${IND}}\n")
      append('}')
    }
  }
}

private const val IND: String = "    "
private const val IND2: String = IND + IND
