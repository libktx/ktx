package ktx.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import ktx.assets.MockAssetLoader.MockParameter
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests asset-related utilities and [AssetManager] extensions.
 */
class AssetsTest {
  val assetManager: AssetManager = managerWithMockAssetLoader()

  @Before
  fun `mock files`() {
    Gdx.files = MockFiles()
  }

  @Test
  fun `should schedule asset loading`() {
    val assetWrapper = assetManager.load<MockAsset>("test")
    assetManager.loadAll()

    assertTrue(assetWrapper.isLoaded())
    val asset = assetWrapper.asset
    assertTrue(asset is MockAsset)
    assertSame(assetManager["test"], asset)
    assertEquals("test", asset.data)
  }

  @Test
  fun `should schedule asset loading with parameters`() {
    val assetWrapper = assetManager.load("test", MockParameter("additional"))
    assetManager.loadAll()

    assertTrue(assetWrapper.isLoaded())
    val asset = assetWrapper.asset
    assertTrue(asset is MockAsset)
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
    assertEquals("additional", asset.additional)
  }

  @Test
  fun `should schedule asset loading with AssetDescriptor`() {
    val assetWrapper = assetManager.loadAsset(assetDescriptor<MockAsset>("test"))
    assetManager.loadAll()

    assertTrue(assetWrapper.isLoaded())
    val asset = assetWrapper.asset
    assertTrue(asset is MockAsset)
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
  }

  @Test
  fun `should load on demand`() {
    val assetWrapper = assetManager.loadOnDemand<MockAsset>("test")

    assertFalse(assetWrapper.isLoaded())
    assetManager.loadAll()
    // Loading was not supposed to be scheduled, should be still unloaded:
    assertFalse(assetWrapper.isLoaded())
    // Loaded on first asset getter call:
    val asset = assetWrapper.asset
    assertTrue(assetWrapper.isLoaded())
    assertTrue(asset is MockAsset)
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
  }

  @Test
  fun `should load on demand with parameters`() {
    val assetWrapper = assetManager.loadOnDemand("test", MockParameter("additional"))

    assertFalse(assetWrapper.isLoaded())
    assetManager.loadAll()
    // Loading was not supposed to be scheduled, should be still unloaded:
    assertFalse(assetWrapper.isLoaded())
    // Loaded on first asset getter call:
    val asset = assetWrapper.asset
    assertTrue(assetWrapper.isLoaded())
    assertTrue(asset is MockAsset)
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
    assertEquals("additional", asset.additional)
  }

  @Test
  fun `should load on demand with descriptor`() {
    val assetWrapper = assetManager.loadOnDemand(assetDescriptor<MockAsset>("test"))

    assertFalse(assetWrapper.isLoaded())
    assetManager.loadAll()
    // Loading was not supposed to be scheduled, should be still unloaded:
    assertFalse(assetWrapper.isLoaded())
    // Loaded on first asset getter call:
    val asset = assetWrapper.asset
    assertTrue(assetWrapper.isLoaded())
    assertTrue(asset is MockAsset)
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
  }

  @Test
  fun `should extract loaded asset with explicit type from manager`() {
    assetManager.load<MockAsset>("test")
    assetManager.loadAll()

    val asset = assetManager.getAsset<MockAsset>("test")
    assertTrue(asset is MockAsset)
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
  }

  @Test(expected = GdxRuntimeException::class)
  fun `should fail to extract unloaded asset`() {
    assetManager.getAsset<MockAsset>("unloaded")
  }

  @Test
  fun `should unload asset gracefully`() {
    assetManager.load<MockAsset>("test")
    assetManager.loadAll()
    val asset = assetManager.getAsset<MockAsset>("test")

    assetManager.unloadSafely("test")
    assertFalse(assetManager.isLoaded("test"))
    assertTrue(asset.disposed)
  }

  @Test
  fun `should ignore unloading exception due to asset not being loaded`() {
    assetManager.unloadSafely("unloaded") // unload("unloaded") would throw exception.
  }

  @Test
  fun `should ignore exceptions thrown during unloading`() {
    val assetManager = mock<AssetManager> {
      on { unload("test") } doThrow GdxRuntimeException("Expected.")
    }
    assetManager.unloadSafely("test")
    verify(assetManager).unload("test")
  }

  @Test
  fun `should ignore exception due to multiple asset unload requests`() {
    assetManager.load<MockAsset>("test")
    assetManager.loadAll()

    assetManager.unloadSafely("test")
    assetManager.unloadSafely("test")
    assetManager.unloadSafely("test")
    assertFalse(assetManager.isLoaded("test"))
  }

  @Test
  fun `should unload asset handling exception`() {
    assetManager.load<MockAsset>("test")
    assetManager.loadAll()
    val asset = assetManager.getAsset<MockAsset>("test")

    assetManager.unload("test") {
      fail("Exception should not be thrown.")
    }
    assertFalse(assetManager.isLoaded("test"))
    assertTrue(asset.disposed)
  }

  @Test
  fun `should handle unloading exception due to asset not being loaded`() {
    var exception: Exception? = null
    assetManager.unload("unloaded") { expected ->
      assertTrue(expected is GdxRuntimeException)
      exception = expected
    }
    assertNotNull(exception)
  }

  @Test
  fun `should handle exceptions thrown during unloading`() {
    val assetManager = mock<AssetManager> {
      on { unload("test") } doThrow GdxRuntimeException("Expected.")
    }
    assetManager.unload("test") { exception ->
      assertTrue(exception is GdxRuntimeException)
    }
    verify(assetManager).unload("test")
  }

  @Test
  fun `should delegate field to scheduled asset`() {
    class TestDelegate {
      val asset by assetManager.load<MockAsset>("test")
    }

    val test = TestDelegate()
    assetManager.loadAll()

    assertTrue(test.asset is MockAsset)
    assertEquals(assetManager["test"], test.asset)
    assertEquals("test", test.asset.data)
  }

  @Test
  fun `should delegate local variable to scheduled asset`() {
    val asset by assetManager.load<MockAsset>("test")
    assetManager.loadAll()

    assertTrue(asset is MockAsset)
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
  }

  @Test(expected = GdxRuntimeException::class)
  fun `should throw if using delegate field before loading`() {
    class TestDelegate {
      val asset by assetManager.load<MockAsset>("test")
    }

    val test = TestDelegate()
    test.asset.data
  }

  @Test(expected = GdxRuntimeException::class)
  fun `should throw if using delegate local variable before loading`() {
    val asset by assetManager.load<MockAsset>("test")
    asset.data
  }

  @Test
  fun `should delegate field to eagerly loaded asset`() {
    class TestDelegate {
      val asset by assetManager.loadOnDemand<MockAsset>("test")
    }

    val test = TestDelegate()

    // Should be loaded after first usage:
    assertFalse(assetManager.isLoaded("test"))
    assertTrue(test.asset is MockAsset)
    // Previous check should have loaded the asset:
    assertTrue(assetManager.isLoaded("test"))
    assertEquals(assetManager["test"], test.asset)
    assertEquals("test", test.asset.data)
    assertTrue(test.asset === test.asset) // Is instance cached?
  }

  @Test
  fun `should delegate local variable to eagerly loaded asset`() {
    val asset by assetManager.loadOnDemand<MockAsset>("test")

    // Should be loaded after first usage:
    assertFalse(assetManager.isLoaded("test"))
    assertTrue(asset is MockAsset)
    // Previous check should have loaded the asset:
    assertTrue(assetManager.isLoaded("test"))
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
  }

  @Test
  fun `should delegate field to scheduled asset with descriptor`() {
    class TestDelegate {
      val asset by assetManager.loadAsset(assetDescriptor<MockAsset>("test"))
    }

    val test = TestDelegate()
    assetManager.finishLoading()

    assertTrue(test.asset is MockAsset)
    assertEquals(assetManager["test"], test.asset)
    assertEquals("test", test.asset.data)
  }

  @Test
  fun `should delegate local variable to scheduled asset with descriptor`() {
    val asset by assetManager.loadAsset(assetDescriptor<MockAsset>("test"))
    assetManager.finishLoading()

    assertTrue(asset is MockAsset)
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
  }

  @Test(expected = GdxRuntimeException::class)
  fun `should throw if using delegate field before loading with descriptor`() {
    class TestDelegate {
      val asset by assetManager.loadAsset(assetDescriptor<MockAsset>("test"))
    }

    val test = TestDelegate()
    test.asset.data
  }

  @Test(expected = GdxRuntimeException::class)
  fun `should throw if using delegate local variable before loading with descriptor`() {
    val asset by assetManager.loadAsset(assetDescriptor<MockAsset>("test"))
    asset.data
  }

  @Test
  fun `should delegate field to eagerly loaded asset with descriptor`() {
    class TestDelegate {
      val asset by assetManager.loadOnDemand(assetDescriptor<MockAsset>("test"))
    }

    val test = TestDelegate()

    // Should be loaded after first usage:
    assertFalse(assetManager.isLoaded("test"))
    assertTrue(test.asset is MockAsset)
    // Previous check should have loaded the asset:
    assertTrue(assetManager.isLoaded("test"))
    assertEquals(assetManager["test"], test.asset)
    assertEquals("test", test.asset.data)
    assertTrue(test.asset === test.asset) // Is instance cached?
  }

  @Test
  fun `should delegate local variable to eagerly loaded asset with descriptor`() {
    val asset by assetManager.loadOnDemand(assetDescriptor<MockAsset>("test"))

    // Should be loaded after first usage:
    assertFalse(assetManager.isLoaded("test"))
    assertTrue(asset is MockAsset)
    // Previous check should have loaded the asset:
    assertTrue(assetManager.isLoaded("test"))
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
  }

  @Test
  fun `should create asset descriptor with string path`() {
    val descriptor = assetDescriptor<MockAsset>("mock.file")

    assertEquals(MockAsset::class.java, descriptor.type)
    assertEquals("mock.file", descriptor.fileName)
  }

  @Test
  fun `should create asset description with string path and loading parameters`() {
    val parameter = MockParameter("mock")
    val descriptorWithParams = assetDescriptor("mock.file", parameter)

    assertEquals(MockAsset::class.java, descriptorWithParams.type)
    assertEquals("mock.file", descriptorWithParams.fileName)
    assertEquals(parameter, descriptorWithParams.params)
  }

  @Test
  fun `should create asset descriptor with FileHandle`() {
    val descriptor = assetDescriptor<MockAsset>(FileHandle("mock.file"))

    assertEquals(MockAsset::class.java, descriptor.type)
    assertEquals("mock.file", descriptor.file.path())
  }

  @Test
  fun `should create asset descriptor with FileHandle and loading parameters`() {
    val parameter = MockParameter("mock")
    val descriptorWithParams = assetDescriptor(FileHandle("mock.file"), parameter)

    assertEquals(MockAsset::class.java, descriptorWithParams.type)
    assertEquals("mock.file", descriptorWithParams.file.path())
    assertEquals(parameter, descriptorWithParams.params)
  }

  @Test
  fun `should find no AssetLoader for unknown asset type`() {
    val manager = AssetManager()
    assertNull(manager.getLoader<MockAsset>())
  }

  @Test
  fun `should find no AssetLoader for unknown suffix`() {
    val manager = AssetManager()
    val loader = MockAssetLoader(manager.fileHandleResolver)
    manager.setLoader(loader, "suffix")

    assertNull(manager.getLoader<MockAsset>("unknown"))
  }

  @Test
  fun `should find AssetLoader`() {
    val manager = AssetManager()
    val loader = MockAssetLoader(manager.fileHandleResolver)
    manager.setLoader(loader)

    assertSame(loader, manager.getLoader<MockAsset>())
  }

  @Test
  fun `should find AssetLoader with suffix`() {
    val manager = AssetManager()
    val loader = MockAssetLoader(manager.fileHandleResolver)
    manager.setLoader(loader, "suffix")

    assertSame(loader, manager.getLoader<MockAsset>("suffix"))
    assertNull(manager.getLoader<MockAsset>())
  }

  @Test
  fun `should set AssetLoader`() {
    val manager = AssetManager()
    val loader = MockAssetLoader(manager.fileHandleResolver)
    manager.setLoader(loader)

    assertSame(loader, manager.getLoader(MockAsset::class.java))
  }

  @Test
  fun `should set AssetLoader with suffix`() {
    val manager = AssetManager()
    val loader = MockAssetLoader(manager.fileHandleResolver)
    manager.setLoader(loader, ".mock")

    assertSame(loader, manager.getLoader(MockAsset::class.java, ".mock"))
  }
}

/**
 * Creates an [AssetManager] with registered [MockAssetLoader].
 */
private fun managerWithMockAssetLoader() = AssetManager().apply {
  setLoader(MockAssetLoader(fileHandleResolver))
}

/**
 * Spin-waits on [AssetManager.update].
 */
private fun AssetManager.loadAll() {
  while (!update()) {
  }
}

/**
 * Represents a mock-up asset. Implements [Disposable] for testing utility.
 * @param data path of the file.
 * @param additional optional string value passed with [MockParameter].
 */
class MockAsset(val data: String, val additional: String?) : Disposable {
  var disposed = false
  override fun dispose() {
    disposed = true
  }
}

/**
 * Mocks asynchronous file loading. Sets [MockAsset.data] as file path. Extracts [MockAsset.additional] from
 * [MockParameter] (if present).
 */
class MockAssetLoader(fileHandleResolver: FileHandleResolver) :
    AsynchronousAssetLoader<MockAsset, MockParameter>(fileHandleResolver) {
  @Volatile private var additional: String? = null
  override fun loadAsync(manager: AssetManager, fileName: String, file: FileHandle, parameter: MockParameter?) {
    if (parameter != null) {
      additional = parameter.additional
    }
  }

  override fun loadSync(manager: AssetManager, fileName: String,
                        file: FileHandle, parameter: MockParameter?): MockAsset {
    val asset = MockAsset(file.path(), additional)
    additional = null
    return asset
  }

  override fun getDependencies(fileName: String?, file: FileHandle?, parameter: MockParameter?):
      Array<AssetDescriptor<Any>>? = null

  /** Allows to set [MockAsset.additional] via loader. Tests assets parameters API. */
  class MockParameter(val additional: String?) : AssetLoaderParameters<MockAsset>()
}

@Suppress("DEPRECATION")
@Deprecated("Tests deprecated API.")
class GlobalAssetManagerTest {
  @Before
  fun mockFiles() {
    Gdx.files = MockFiles()
    // Clearing global AssetManager and adding MockAsset loader:
    Assets.manager.clear()
    Assets.manager.setLoader(MockAsset::class.java, MockAssetLoader(Assets.manager.fileHandleResolver))
  }

  @Test
  fun shouldScheduleAssetLoading() {
    val assetWrapper = load<MockAsset>("test")
    assertFalse(assetWrapper.isLoaded())
    finishLoading()
    assertTrue(assetWrapper.isLoaded())
    val asset = assetWrapper.asset
    assertTrue(asset is MockAsset)
    assertEquals(Assets.manager.get("test", MockAsset::class.java), asset)
    assertEquals("test", asset.data)
  }

  @Test
  fun shouldScheduleAssetLoadingWithParameters() {
    // Note that loading parameters instance allows to omit generic parameter.
    val assetWrapper = load("test", MockParameter("additional"))
    assertFalse(assetWrapper.isLoaded())
    finishLoading()
    assertTrue(assetWrapper.isLoaded())
    val asset = assetWrapper.asset
    assertTrue(asset is MockAsset)
    assertEquals(Assets.manager.get("test", MockAsset::class.java), asset)
    assertEquals("test", asset.data)
    assertEquals("additional", asset.additional)
  }

  @Test
  fun shouldScheduleAssetLoadingWithDescriptor() {
    // Note that asset descriptor instance allows to omit generic parameter in load.
    val assetWrapper = load(assetDescriptor<MockAsset>("test"))
    assertFalse(assetWrapper.isLoaded())
    finishLoading()
    assertTrue(assetWrapper.isLoaded())
    val asset = assetWrapper.asset
    assertTrue(asset is MockAsset)
    assertEquals(Assets.manager.get("test", MockAsset::class.java), asset)
    assertEquals("test", asset.data)
  }

  @Test
  fun shouldLoadOnDemand() {
    val assetWrapper = loadOnDemand<MockAsset>("test")
    assertFalse(assetWrapper.isLoaded())
    finishLoading() // Loading was not supposed to be scheduled, should be still unloaded.
    assertFalse(assetWrapper.isLoaded())

    val asset = assetWrapper.asset
    assertTrue(assetWrapper.isLoaded())
    assertTrue(asset is MockAsset)
    assertEquals(Assets.manager.get("test", MockAsset::class.java), asset)
    assertEquals("test", asset.data)
  }

  @Test
  fun shouldLoadOnDemandWithParameters() {
    val assetWrapper = loadOnDemand("test", MockParameter("additional"))
    assertFalse(assetWrapper.isLoaded())
    finishLoading() // Loading was not supposed to be scheduled, should be still unloaded.
    assertFalse(assetWrapper.isLoaded())

    val asset = assetWrapper.asset
    assertTrue(assetWrapper.isLoaded())
    assertTrue(asset is MockAsset)
    assertEquals(Assets.manager.get("test", MockAsset::class.java), asset)
    assertEquals("test", asset.data)
    assertEquals("additional", asset.additional)
  }

  @Test
  fun shouldLoadOnDemandWithDescriptor() {
    val assetWrapper = loadOnDemand(assetDescriptor<MockAsset>("test"))
    assertFalse(assetWrapper.isLoaded())
    finishLoading() // Loading was not supposed to be scheduled, should be still unloaded.
    assertFalse(assetWrapper.isLoaded())

    val asset = assetWrapper.asset
    assertTrue(assetWrapper.isLoaded())
    assertTrue(asset is MockAsset)
    assertEquals(Assets.manager.get("test", MockAsset::class.java), asset)
    assertEquals("test", asset.data)
  }

  @Test
  fun shouldExtractLoadedAssetFromManager() {
    Assets.manager.load("test", MockAsset::class.java)
    finishLoading()
    val asset = asset<MockAsset>("test")
    assertTrue(asset is MockAsset)
    assertEquals(Assets.manager.get("test", MockAsset::class.java), asset)
    assertEquals("test", asset.data)
  }

  @Test(expected = GdxRuntimeException::class)
  fun shouldFailToExtractUnloadedAsset() {
    asset<MockAsset>("unloaded")
  }

  @Test
  fun shouldExtractLoadedAssetFromManagerWithDescriptor() {
    Assets.manager.load("test", MockAsset::class.java)
    finishLoading()
    val asset = asset(assetDescriptor<MockAsset>("test"))
    assertTrue(asset is MockAsset)
    assertEquals(Assets.manager.get("test", MockAsset::class.java), asset)
    assertEquals("test", asset.data)
  }

  @Test(expected = GdxRuntimeException::class)
  fun shouldFailToExtractUnloadedAssetWithDescriptor() {
    asset(assetDescriptor<MockAsset>("unloaded"))
  }

  @Test
  fun shouldReportIfLoaded() {
    Assets.manager.load("test", MockAsset::class.java)
    assertFalse(isLoaded<MockAsset>("test"))

    finishLoading()
    assertTrue(isLoaded<MockAsset>("test"))

    Assets.manager.unload("test")
    assertFalse(isLoaded<MockAsset>("test"))
  }

  @Test
  fun shouldUnloadAsset() {
    Assets.manager.load("test", MockAsset::class.java)
    finishLoading()
    assertTrue(Assets.manager.isLoaded("test", MockAsset::class.java))
    val asset = Assets.manager.get("test", MockAsset::class.java)
    assertFalse(asset.disposed)

    unload("test")
    assertFalse(Assets.manager.isLoaded("test", MockAsset::class.java))
    assertTrue(asset.disposed)
  }

  @Test
  fun shouldAttemptToUnloadAndIgnoreFailureEvenIfAssetNotLoaded() {
    unload("test") // Assets.manager.unload("test") would normally throw an exception.
  }

  @Test
  fun shouldDelegateFieldToScheduledAsset() {
    class TestDelegate {
      val asset by load<MockAsset>("test")
    }

    val test = TestDelegate()
    finishLoading()
    assertTrue(test.asset is MockAsset)
    assertEquals(Assets.manager.get("test", MockAsset::class.java), test.asset)
    assertEquals("test", test.asset.data)
  }

  @Test(expected = GdxRuntimeException::class)
  fun shouldThrowIfUsingDelegateFieldBeforeLoading() {
    class TestDelegate {
      val asset by load<MockAsset>("test")
    }

    val test = TestDelegate()
    test.asset.data
  }

  @Test
  fun shouldDelegateFieldToEagerlyLoadedAsset() {
    class TestDelegate {
      val asset by loadOnDemand<MockAsset>("test")
    }

    val test = TestDelegate()
    assertTrue(test.asset is MockAsset)
    assertEquals(Assets.manager.get("test", MockAsset::class.java), test.asset)
    assertEquals("test", test.asset.data)
    assertTrue(test.asset === test.asset) // Caches same instance?
  }

  @Test
  fun shouldDelegateFieldToScheduledAssetWithDescriptor() {
    class TestDelegate {
      val asset by load(assetDescriptor<MockAsset>("test"))
    }

    val test = TestDelegate()
    finishLoading()
    assertTrue(test.asset is MockAsset)
    assertEquals(Assets.manager.get("test", MockAsset::class.java), test.asset)
    assertEquals("test", test.asset.data)
  }

  @Test(expected = GdxRuntimeException::class)
  fun shouldThrowIfUsingDelegateFieldBeforeLoadingWithDescriptor() {
    class TestDelegate {
      val asset by load(assetDescriptor<MockAsset>("test"))
    }

    val test = TestDelegate()
    test.asset.data
  }

  @Test
  fun shouldDelegateFieldToEagerlyLoadedAssetWithDescriptor() {
    class TestDelegate {
      val asset by loadOnDemand(assetDescriptor<MockAsset>("test"))
    }

    val test = TestDelegate()
    assertTrue(test.asset is MockAsset)
    assertEquals(Assets.manager.get("test", MockAsset::class.java), test.asset)
    assertEquals("test", test.asset.data)
    assertTrue(test.asset === test.asset) // Caches same instance?
  }

  @Test
  fun shouldDelegateFieldToAssetLoadedOnDemand() {
    class TestDelegate {
      val asset by loadOnDemand<MockAsset>("test")
    }

    val test = TestDelegate()
    // Note that finishLoading (AssetManager.update) does not have to be called - asset is loaded on first getter call.
    assertTrue(test.asset is MockAsset)
    assertEquals("test", test.asset.data)
  }

  /**
   * Spin-waits on [AssetManager.update].
   */
  private fun finishLoading() {
    Assets.manager.loadAll()
  }
}
