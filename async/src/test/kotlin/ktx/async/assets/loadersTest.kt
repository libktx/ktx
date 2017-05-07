package ktx.async.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import io.kotlintest.mock.mock
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests [AssetLoaderStorage] - manager of [AssetLoader] instances used by an [AssetStorage] to load assets.
 */
class AssetLoaderStorageTest {
  @Test
  fun `should return null when requested to return loader for unknown type`() {
    val storage = AssetLoaderStorage()

    val loader = storage.getLoader(String::class.java)

    assertNull(loader)
  }

  @Test
  fun `should return null when requested to return loader with suffix for unknown type`() {
    val storage = AssetLoaderStorage()

    val loader = storage.getLoader(String::class.java, ".txt")

    assertNull(loader)
  }

  @Test
  fun `should return loader for known type`() {
    val storage = AssetLoaderStorage()
    val stringLoader = mockStringLoader()
    storage.setLoader(String::class.java, stringLoader)

    val loader = storage.getLoader(String::class.java)

    assertSame(stringLoader, loader)
  }

  @Test
  fun `should return null when requested main loader for known type without appropriate suffix`() {
    val storage = AssetLoaderStorage()
    storage.setLoader(String::class.java, mockStringLoader(), suffix = ".txt")

    val loader = storage.getLoader(String::class.java)

    assertNull(loader)
  }

  @Test
  fun `should return null when requested loader for known type with invalid suffix`() {
    val storage = AssetLoaderStorage()
    storage.setLoader(String::class.java, mockStringLoader(), suffix = ".txt")

    val loader = storage.getLoader(String::class.java, path = "invalid.md")

    assertNull(loader)
  }

  @Test
  fun `should return main loader if suffix does not match specific loader`() {
    val storage = AssetLoaderStorage()
    val mainLoader = mockStringLoader()
    val suffixLoader = mockStringLoader()
    storage.setLoader(String::class.java, mainLoader)
    storage.setLoader(String::class.java, suffixLoader, suffix = ".md")

    val loader = storage.getLoader(String::class.java, path = "test.txt")

    assertSame(mainLoader, loader)
  }

  @Test
  fun `should return loader for known type with valid suffix`() {
    val storage = AssetLoaderStorage()
    val stringLoader = mockStringLoader()
    storage.setLoader(String::class.java, stringLoader, suffix = ".txt")

    val loader = storage.getLoader(String::class.java, path = "test.txt")

    assertSame(stringLoader, loader)
  }

  @Test
  fun `should override main loader`() {
    val storage = AssetLoaderStorage()
    val previousLoader = mockStringLoader()
    val stringLoader = mockStringLoader()
    storage.setLoader(String::class.java, previousLoader)
    storage.setLoader(String::class.java, stringLoader)

    val loader = storage.getLoader(String::class.java)

    assertNotSame(previousLoader, loader)
    assertSame(stringLoader, loader)
  }

  @Test
  fun `should override loader with suffix`() {
    val storage = AssetLoaderStorage()
    val previousLoader = mockStringLoader()
    val stringLoader = mockStringLoader()
    storage.setLoader(String::class.java, previousLoader)
    storage.setLoader(String::class.java, stringLoader)

    val loader = storage.getLoader(String::class.java)

    assertNotSame(previousLoader, loader)
    assertSame(stringLoader, loader)
  }

  @Test(expected = AssetStorageException::class)
  fun `should reject loader that does not extend SynchronousAssetLoader or AsynchronousAssetLoader`() {
    val storage = AssetLoaderStorage()
    val invalidLoader = mock<AssetLoader<String, AssetLoaderParameters<String>>>()

    storage.setLoader(String::class.java, invalidLoader)
  }

  private fun mockStringLoader() = mock<SynchronousAssetLoader<String, AssetLoaderParameters<String>>>()
}
