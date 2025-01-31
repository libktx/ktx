package ktx.assets.async

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.files.FileHandle
import io.kotlintest.matchers.shouldThrow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.io.File
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Tests [AssetLoaderStorage] - manager of [AssetLoader] instances used by an [AssetStorage] to load assets.
 */
class AssetLoaderStorageTest {
  @Test
  fun `should return null when requested to return loader for unknown type`() {
    // Given:
    val storage = AssetLoaderStorage()

    // When:
    val loader = storage.getLoader(String::class.java)

    // Then:
    assertNull(loader)
  }

  @Test
  fun `should return null when requested to return loader with suffix for unknown type`() {
    // Given:
    val storage = AssetLoaderStorage()

    // When:
    val loader = storage.getLoader(String::class.java, ".txt")

    // Then:
    assertNull(loader)
  }

  @Test
  fun `should return loader for known type`() {
    // Given:
    val storage = AssetLoaderStorage()
    val stringLoader = mockStringLoader()
    storage.setLoaderProvider(String::class.java) { stringLoader }

    // When:
    val loader = storage.getLoader(String::class.java)

    // Then:
    assertSame(stringLoader, loader)
  }

  @Test
  fun `should return null when requested main loader for known type without appropriate suffix`() {
    // Given:
    val storage = AssetLoaderStorage()
    storage.setLoaderProvider(String::class.java, suffix = ".txt") { mockStringLoader() }

    // When:
    val loader = storage.getLoader(String::class.java)

    // Then:
    assertNull(loader)
  }

  @Test
  fun `should return null when requested loader for known type with invalid suffix`() {
    // Given:
    val storage = AssetLoaderStorage()
    storage.setLoaderProvider(String::class.java, suffix = ".txt") { mockStringLoader() }

    // When:
    val loader = storage.getLoader(String::class.java, path = "invalid.md")

    // Then:
    assertNull(loader)
  }

  @Test
  fun `should return main loader if suffix does not match specific loader`() {
    // Given:
    val storage = AssetLoaderStorage()
    val mainLoader = mockStringLoader()
    val suffixLoader = mockStringLoader()
    storage.setLoaderProvider(String::class.java) { mainLoader }
    storage.setLoaderProvider(String::class.java, suffix = ".md") { suffixLoader }

    // When:
    val loader = storage.getLoader(String::class.java, path = "test.txt")

    // Then:
    assertSame(mainLoader, loader)
  }

  @Test
  fun `should return loader for known type with valid suffix`() {
    // Given:
    val storage = AssetLoaderStorage()
    val stringLoader = mockStringLoader()
    storage.setLoaderProvider(String::class.java, suffix = ".txt") { stringLoader }

    // When:
    val loader = storage.getLoader(String::class.java, path = "test.txt")

    // Then:
    assertSame(stringLoader, loader)
  }

  @Test
  fun `should override main loader`() {
    // Given:
    val storage = AssetLoaderStorage()
    val previousLoader = mockStringLoader()
    val stringLoader = mockStringLoader()
    storage.setLoaderProvider(String::class.java) { previousLoader }
    storage.setLoaderProvider(String::class.java) { stringLoader }

    // When:
    val loader = storage.getLoader(String::class.java)

    // Then:
    assertNotSame(previousLoader, loader)
    assertSame(stringLoader, loader)
  }

  @Test
  fun `should override loader with suffix`() {
    // Given:
    val storage = AssetLoaderStorage()
    val previousLoader = mockStringLoader()
    val stringLoader = mockStringLoader()
    storage.setLoaderProvider(String::class.java) { previousLoader }
    storage.setLoaderProvider(String::class.java) { stringLoader }

    // When:
    val loader = storage.getLoader(String::class.java)

    // Then:
    assertNotSame(previousLoader, loader)
    assertSame(stringLoader, loader)
  }

  @Test
  fun `should not cache loaders and invoke loader provider for each request`() {
    // Given:
    val storage = AssetLoaderStorage()
    storage.setLoaderProvider(String::class.java) { mockStringLoader() }

    // When:
    val firstLoader = storage.getLoader(String::class.java)
    val secondLoader = storage.getLoader(String::class.java)

    // Then:
    assertNotSame(firstLoader, secondLoader)
  }

  @Test
  fun `should reject loader that does not extend SynchronousAssetLoader or AsynchronousAssetLoader`() {
    // Given:
    val storage = AssetLoaderStorage()
    val invalidLoader = mock<AssetLoader<String, AssetLoaderParameters<String>>>()

    // Expect:
    shouldThrow<InvalidLoaderException> {
      storage.setLoaderProvider(String::class.java) { invalidLoader }
    }
  }

  private fun mockStringLoader() = mock<SynchronousAssetLoader<String, AssetLoaderParameters<String>>>()
}

/**
 * Tests [AssetLoader] extensions.
 */
class LoadersTest {
  @Test
  fun `should get dependencies`() {
    // Given:
    val dependencies = GdxArray<AssetDescriptor<*>>()
    val file = FileHandle(File("test"))
    val assetDescriptor = AssetDescriptor("test", String::class.java)
    assetDescriptor.file = file
    val loader =
      mock<Loader<String>> {
        on(it.getDependencies("test", file, null)) doReturn dependencies
      }

    // When:
    val result = loader.getDependencies(assetDescriptor)

    // Then:
    assertSame(dependencies, result)
    verify(loader).getDependencies("test", file, null)
  }

  @Test
  fun `should load synchronously`() {
    // Given:
    val assetManager = mock<AssetManager>()
    val file = FileHandle(File("test"))
    val assetDescriptor = AssetDescriptor("test", String::class.java)
    assetDescriptor.file = file
    val loader =
      mock<SynchronousLoader<String>> {
        on(it.load(assetManager, "test", file, null)) doReturn "Asset."
      }

    // When:
    val asset = loader.load(assetManager, assetDescriptor)

    // Then:
    assertSame("Asset.", asset)
    verify(loader).load(assetManager, "test", file, null)
  }

  @Test
  fun `should load asynchronously`() {
    // Given:
    val assetManager = mock<AssetManager>()
    val file = FileHandle(File("test"))
    val assetDescriptor = AssetDescriptor("test", String::class.java)
    assetDescriptor.file = file
    val loader =
      mock<AsynchronousLoader<String>> {
        on(it.loadSync(assetManager, "test", file, null)) doReturn "Asset."
      }

    // When:
    loader.loadAsync(assetManager, assetDescriptor)
    val asset = loader.loadSync(assetManager, assetDescriptor)

    // Then:
    assertSame("Asset.", asset)
    verify(loader).loadAsync(assetManager, "test", file, null)
    verify(loader).loadSync(assetManager, "test", file, null)
  }
}

/**
 * Tests [ManualLoader] implementation.
 */
class ManualLoaderTest {
  @Test
  fun `should return empty dependencies array`() {
    // When:
    val dependencies = ManualLoader.getDependencies("file.path", mock(), null)

    // Then:
    assertEquals(GdxArray<AssetDescriptor<*>>(0), dependencies)
  }

  @Test
  fun `should return empty dependencies array with loading parameters`() {
    // When:
    val dependencies =
      ManualLoader.getDependencies(
        "file.path",
        mock(),
        ManualLoadingParameters(),
      )

    // Then:
    assertEquals(GdxArray<AssetDescriptor<*>>(0), dependencies)
  }
}
