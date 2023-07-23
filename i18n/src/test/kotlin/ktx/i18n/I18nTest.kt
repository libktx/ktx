package ktx.i18n

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.I18NBundle
import io.kotlintest.mock.mock
import ktx.i18n.I18nTest.BundleEnum.key
import ktx.i18n.I18nTest.BundleEnum.keyWithArgument
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Tests internationalization-related classes and functions stored in *i18n.kt*.
 */
class I18nTest {
  private val bundleContent = """
key=Value.
keyWithArgument=Value with {0} argument.
"""
  private val bundleFile = FileHandle(File.createTempFile("nls", ".properties"))
  private var bundle: I18NBundle = mock()

  @Before
  fun `create I18NBundle`() {
    bundleFile.writeString(bundleContent, false)
    bundle = I18NBundle.createBundle(bundleFile.sibling(bundleFile.nameWithoutExtension()))
    BundleEnum.i18nBundle = bundle
  }

  @Test
  fun `should access bundle lines with brace operator`() {
    assertEquals("Value.", bundle["key"])
  }

  @Test
  fun `should format bundle lines with brace operator and arguments`() {
    assertEquals("Value with 1 argument.", bundle["keyWithArgument", 1])
  }

  @Test
  fun `should access values of BundleLine instances with brace operator`() {
    assertEquals("Value.", bundle[key])
  }

  @Test
  fun `should format values of BundleLine instances with brace operator and arguments`() {
    assertEquals("Value with 1 argument.", bundle[keyWithArgument, 1])
  }

  @Test
  fun `should find BundleLine value with nls property`() {
    assertEquals("Value.", key.nls)
  }

  @Test
  fun `should format BundleLine value using nls method with arguments`() {
    assertEquals("Value with 1 argument.", keyWithArgument.nls(1))
  }

  @Test
  fun `should find BundleLine value with invocation syntax`() {
    assertEquals("Value.", key())
  }

  @Test
  fun `should format BundleLine value with invocation syntax and arguments`() {
    assertEquals("Value with 1 argument.", keyWithArgument(1))
  }

  @Test(expected = NotImplementedError::class)
  fun `should prohibit bundle property access when not overridden`() {
    val bundleLine = object : BundleLine {}

    bundleLine.bundle
  }

  @After
  fun `delete temporary file`() {
    bundleFile.delete()
  }

  /** For [BundleLine] tests. */
  @Suppress("ktlint:standard:enum-entry-name-case")
  internal enum class BundleEnum : BundleLine {
    /** "Value." */
    key,

    /** "Value with {0} argument." */
    keyWithArgument,

    ;

    override val bundle: I18NBundle
      get() = i18nBundle!!

    companion object {
      var i18nBundle: I18NBundle? = null
    }
  }
}
