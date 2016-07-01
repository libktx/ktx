package ktx.i18n

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.I18NBundle
import ktx.i18n.I18nTest.Bundle.key1
import ktx.i18n.I18nTest.Bundle.key2
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.io.File
import java.util.Locale

/**
 * Tests internationalization-related classes and functions stored in *i18n.kt*.
 * @author MJ
 */
class I18nTest {
  val bundleContent = """
key1=Value.
key2=Value with {0} argument.
"""
  val bundleFile = FileHandle(File.createTempFile("nls", ".properties"))
  private fun getBundleRootFileHandle() = bundleFile.sibling(bundleFile.nameWithoutExtension())

  @Before
  fun createBundleFile() {
    bundleFile.writeString(bundleContent, false)
  }

  @Test
  fun shouldFindBundleLine() {
    val bundle = I18NBundle.createBundle(getBundleRootFileHandle())
    assertEquals("Value.", nls("key1", bundle))
  }

  @Test
  fun shouldFindBundleLineInDefaultBundle() {
    I18n.defaultBundle = I18NBundle.createBundle(getBundleRootFileHandle())
    assertEquals("Value.", nls("key1"))
  }

  @Test
  fun shouldFormatBundleLine() {
    val bundle = I18NBundle.createBundle(getBundleRootFileHandle())
    assertEquals("Value with one argument.", nls("key2", args = "one", bundle = bundle))
  }

  @Test
  fun shouldFormatBundleLineFromDefaultBundle() {
    I18n.defaultBundle = I18NBundle.createBundle(getBundleRootFileHandle())
    assertEquals("Value with one argument.", nls("key2", "one"))
    assertEquals("Value with 1 argument.", nls("key2", args = 1))
  }

  @Test
  fun shouldLoadBundles() {
    Gdx.files = Mockito.mock(Files::class.java)
    val path = "mock/path"
    Mockito.`when`(Gdx.files.getFileHandle(Mockito.eq(path), Mockito.any())).thenReturn(getBundleRootFileHandle())
    I18n.load(path, Locale.ENGLISH)

    assertNotNull(I18n.defaultBundle)
    assertNotNull(I18n.defaultBundle!!.get("key1"))
  }

  @Test
  fun shouldInvokeBundleReloadListeners() {
    var invoked = false
    I18n.addListener { invoked = true }
    I18n.defaultBundle = null // Invokes setter, should trigger listeners.
    assertTrue(invoked)
  }

  @Test
  fun shouldRemoveListeners() {
    var amount = 0
    val listener: (I18NBundle?) -> Unit = { amount++ }
    I18n.addListener(listener)

    I18n.defaultBundle = null // Invokes setter, should trigger listeners.
    assertEquals(1, amount)

    I18n.removeListener(listener)
    I18n.defaultBundle = null // Invokes setter, should trigger listeners.
    assertEquals(1, amount)
  }


  @Test
  fun shouldClearListeners() {
    var amount = 0
    I18n.addListener { amount++ }
    I18n.addListener { amount++ }

    I18n.defaultBundle = null // Invokes setter, should trigger listeners.
    assertEquals(2, amount)

    I18n.clearListeners()
    I18n.defaultBundle = null // Invokes setter, should trigger listeners.
    assertEquals(2, amount)
  }

  /**
   * [BundleLine] test.
   * @author MJ
   */
  internal enum class Bundle : BundleLine {
    key1,
    key2
  }

  @Test
  fun shouldGetLineValue() {
    I18n.defaultBundle = I18NBundle.createBundle(getBundleRootFileHandle())
    assertEquals("Value.", key1()) // Static import of enum variables.
    assertEquals("Value.", nls(key1))
  }

  @Test
  fun shouldFormatLineValueWithArguments() {
    I18n.defaultBundle = I18NBundle.createBundle(getBundleRootFileHandle())
    assertEquals("Value with 1 argument.", key2(1)) // Static import of enum variables.
    assertEquals("Value with 1 argument.", nls(key2, 1))
  }

  @Test
  fun shouldReturnDefaultBundle() {
    I18n.defaultBundle = I18NBundle.createBundle(getBundleRootFileHandle())
    assertEquals(I18n.defaultBundle, key1.bundle)
  }

  @After
  fun deleteTemporaryFile() {
    bundleFile.delete()
  }
}
