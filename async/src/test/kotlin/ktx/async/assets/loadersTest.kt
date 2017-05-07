package ktx.async.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import ktx.async.assets.TextAssetLoader.TextAssetLoaderParameters
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import com.badlogic.gdx.utils.Array as GdxArray

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

/**
 * Tests [TextAssetLoader]: [AssetLoader] implementation for asynchronous reading of text files.
 */
class TextAssetLoaderTest {
  @Test
  fun `should read text data from FileHandle`() {
    val loader = TextAssetLoader(ClasspathFileHandleResolver(), charset = "UTF-8")
    val file = mock<FileHandle> {
      on(it.readString("UTF-8")) doReturn "Content."
    }

    val result = loader.load(mock(), "test.txt", file, null)

    assertEquals("Content.", result)
    verify(file).readString("UTF-8")
  }

  @Test
  fun `should read text data from FileHandle with loading parameters`() {
    val loader = TextAssetLoader(ClasspathFileHandleResolver(), charset = "UTF-8")
    val file = mock<FileHandle> {
      on(it.readString("UTF-16")) doReturn "Content."
    }

    val result = loader.load(mock(), "test.txt", file, TextAssetLoaderParameters(charset = "UTF-16"))

    assertEquals("Content.", result)
    verify(file).readString("UTF-16")
  }
}

/**
 * Tests [AssetLoader] extensions.
 */
class LoadersTest {
  @Test
  fun `should get dependencies`() {
    val dependencies = GdxArray<AssetDescriptor<*>>()
    val file = FileHandle(File("test"))
    val assetDescriptor = AssetDescriptor("test", String::class.java)
    assetDescriptor.file = file
    val loader = mock<Loader<String>> {
      on(it.getDependencies("test", file, null)) doReturn dependencies
    }

    val result = loader.getDependencies(assetDescriptor)

    assertSame(dependencies, result)
    verify(loader).getDependencies("test", file, null)
  }

  @Test
  fun `should load synchronously`() {
    val assetManager = mock<AssetManager>()
    val file = FileHandle(File("test"))
    val assetDescriptor = AssetDescriptor("test", String::class.java)
    assetDescriptor.file = file
    val loader = mock<SynchronousLoader<String>> {
      on(it.load(assetManager, "test", file, null)) doReturn "Asset."
    }

    val asset = loader.load(assetManager, assetDescriptor)

    assertSame("Asset.", asset)
    verify(loader).load(assetManager, "test", file, null)
  }

  @Test
  fun `should load asynchronously`() {
    val assetManager = mock<AssetManager>()
    val file = FileHandle(File("test"))
    val assetDescriptor = AssetDescriptor("test", String::class.java)
    assetDescriptor.file = file
    val loader = mock<AsynchronousLoader<String>> {
      on(it.loadSync(assetManager, "test", file, null)) doReturn "Asset."
    }

    loader.loadAsync(assetManager, assetDescriptor)
    val asset = loader.loadSync(assetManager, assetDescriptor)

    assertSame("Asset.", asset)
    verify(loader).loadAsync(assetManager, "test", file, null)
    verify(loader).loadSync(assetManager, "test", file, null)
  }
}
