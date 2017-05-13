package ktx.async.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.async.AsyncExecutor
import com.nhaarman.mockito_kotlin.*
import io.kotlintest.matchers.shouldThrow
import ktx.async.`coroutine test`
import org.junit.Assert.*
import org.junit.Test
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Tests [AssetManagerWrapper]: adapter of [AssetStorage] to the [AssetManager] API.
 */
class AssetManagerWrapperTest {
  private var storage = spy(AssetStorage(executor = AsyncExecutor(1), fileResolver = AbsoluteFileHandleResolver()))
  private var wrapper = AssetManagerWrapper(storage)

  @Test
  fun `should delegate 'clear'`() {
    // Inlined methods cannot be verified directly.
    val disposed = mock<Disposable>()
    storage.assets.put("test", disposed)

    wrapper.clear()

    verify(disposed).dispose()
  }

  @Test
  fun `should delegate 'dispose'`() {
    // Inlined methods cannot be verified directly.
    val disposed = mock<Disposable>()
    storage.assets.put("test", disposed)

    wrapper.clear()

    verify(disposed).dispose()
  }

  @Test
  fun `should delegate 'containsAsset'`() {
    val present = "present"
    val absent = "absent"
    storage.assets.put("test", present)

    assertTrue(wrapper.containsAsset(present))
    assertFalse(wrapper.containsAsset(absent))
  }

  @Test
  fun `should delegate 'get'`() {
    val asset = mock<Any>()
    storage.assets.put("test", asset)

    assertSame(asset, wrapper.get<Any>("test"))
    assertSame(asset, wrapper.get("test", Any::class.java))
    assertSame(asset, wrapper.get(AssetDescriptor("test", Any::class.java)))
    shouldThrow<AssetStorageException> { wrapper.get<Any>("absent") }
    shouldThrow<AssetStorageException> { wrapper.get("absent", Any::class.java) }
    shouldThrow<AssetStorageException> { wrapper.get(AssetDescriptor("absent", Any::class.java)) }
  }

  @Test
  fun `should delegate 'getAssetNames'`() {
    storage.assets.put("test", "")

    assertEquals(GdxArray.with("test"), wrapper.assetNames)
  }

  @Test
  fun `should delegate 'getAssetType'`() {
    storage.assets.put("test", "")

    assertEquals(String::class.java, wrapper.getAssetType("test"))
    assertNull(wrapper.getAssetType("absent"))
  }

  @Test
  fun `should delegate 'getAll'`() {
    storage.assets.put("test", "result")
    storage.assets.put("invalid", mock())
    val out = GdxArray<String>()

    val strings = wrapper.getAll(String::class.java, out)

    assertEquals(GdxArray.with("result"), strings)
  }

  @Test
  fun `should delegate 'getAssetFileName'`() {
    storage.assets.put("file", "asset")

    assertEquals("file", wrapper.getAssetFileName("asset"))
    assertNull(wrapper.getAssetFileName("absent"))
  }

  @Test
  fun `should delegate 'getDiagnostics'`() {
    assertEquals(storage.toString(), wrapper.diagnostics)
  }

  @Test
  fun `should delegate 'getFileHandleResolver'`() {
    assertSame(storage.fileResolver, wrapper.fileHandleResolver)
  }

  @Test
  fun `should delegate 'getLoadedAssets'`() {
    storage.assets.put("file", "asset")
    assertEquals(1, wrapper.loadedAssets)
    storage.assets.put("test", "asset")
    assertEquals(2, wrapper.loadedAssets)
  }

  @Test
  fun `should delegate 'getLoader'`() {
    wrapper.getLoader(String::class.java)

    verify(storage).getLoader(String::class.java, "")
  }

  @Test
  fun `should delegate 'getLoader' with file name`() {
    wrapper.getLoader(String::class.java, "test.txt")

    verify(storage).getLoader(String::class.java, "test.txt")
  }

  @Test
  fun `should delegate 'isLoaded'`() {
    wrapper.isLoaded("test")

    verify(storage).isLoaded("test")
  }

  @Test
  fun `should delegate 'isLoader' with class`() {
    storage.assets.put("file", "asset")

    assertTrue(wrapper.isLoaded("file", String::class.java))
    assertFalse(wrapper.isLoaded("file", Int::class.java))
    assertFalse(wrapper.isLoaded("absent", String::class.java))
  }

  @Test
  fun `should delegate 'unload'`() {
    wrapper.unload("test")

    verify(storage).unload("test")
  }

  @Test
  fun `should delegate 'setLoader'`() {
    val loader = mock<SynchronousLoader<String>>()

    wrapper.setLoader(String::class.java, loader)

    verify(storage).setLoader(String::class.java, loader, null)
  }

  @Test
  fun `should delegate 'setLoader' with suffix`() {
    val loader = mock<SynchronousLoader<String>>()

    wrapper.setLoader(String::class.java, ".txt", loader)

    verify(storage).setLoader(String::class.java, loader, ".txt")
  }

  @Test
  fun `should delegate 'getDependencies'`() {
    KStubbing(storage).on(storage.getDependencies("file")) doReturn listOf("a", "b", "c")

    val dependencies = wrapper.getDependencies("file")

    verify(storage).getDependencies("file")
    assertEquals(GdxArray.with("a", "b", "c"), dependencies)
  }

  @Test
  fun `should delegate 'getReferenceCount'`() {
    KStubbing(storage).on(storage.getReferencesCount("file")) doReturn 7

    val referencesCount = wrapper.getReferenceCount("file")

    verify(storage).getReferencesCount("file")
    assertEquals(7, referencesCount)
  }

  @Test
  fun `should delegate 'load'`() = `loading test` { path ->
    wrapper.load(path, String::class.java)
  }

  @Test
  fun `should delegate 'load' with loading parameters`() = `loading test` { path ->
    wrapper.load(path, String::class.java, mock())
  }

  @Test
  fun `should delegate 'load' with descriptor`() = `loading test` { path ->
    wrapper.load(AssetDescriptor(path, String::class.java, mock()))
  }

  // Deprecated no-op methods - that basically always return the same(ish) result, make no sense in context of the
  // asynchronous AssetStorage and are generally never used by AssetLoader implementations - were not tested.

  /**
   * Tests asynchronous asset loading via a coroutine.
   * @param loading should perform loading operation of a string asset located at the given path.
   */
  private inline fun `loading test`(crossinline loading: (path: String) -> Unit)
      = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    storage.setLoader<String>(getStringLoader())
    assertFalse(storage.isLoaded("test"))
    Gdx.files = mock {
      on(it.absolute(any())) doAnswer { MockFileHandle(it.getArgument<String>(0)) }
    }

    ktxAsync {
      loading("test")
      do {
        skipFrame()
      } while (storage.currentlyLoadedAsset != null)

      assertTrue(storage.isLoaded("test"))
      assertEquals("asset-test", storage["test"])

      Gdx.files = null
    }
  }

  private fun getStringLoader(): SynchronousLoader<String>
      = object : SynchronousAssetLoader<String, AssetLoaderParameters<String>>(storage.fileResolver) {
    override fun load(assetManager: AssetManager, fileName: String, file: FileHandle, parameter: AssetLoaderParameters<String>?): String = "asset-$fileName"
    override fun getDependencies(fileName: String, file: FileHandle, parameter: AssetLoaderParameters<String>?): GdxArray<AssetDescriptor<Any>>? = null
  }

  class MockFileHandle(path: String) : FileHandle(path) {
    override fun exists(): Boolean = true
  }
}
