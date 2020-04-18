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
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import ktx.assets.MockAssetLoader.MockParameter
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests asset-related utilities and [AssetManager] extensions.
 */
@Suppress("USELESS_IS_CHECK") // Explicitly checking loaded asset types.
class AssetsTest {
  val assetManager: AssetManager = managerWithMockAssetLoader()

  @Before
  fun `mock files`() {
    Gdx.files = MockFiles()
  }

  @Test
  fun `should schedule asset loading`() {
    val assetWrapper = assetManager.load<MockAsset>("test")
    assetManager.finishLoading()

    assertTrue(assetWrapper.isLoaded())
    val asset = assetWrapper.asset
    assertTrue(asset is MockAsset)
    assertSame(assetManager["test"], asset)
    assertEquals("test", asset.data)
    assertSame(assetManager, (assetWrapper as ManagedAsset).manager)
  }

  @Test
  fun `should schedule asset loading with parameters`() {
    val assetWrapper = assetManager.load("test", MockParameter("additional"))
    assetManager.finishLoading()

    assertTrue(assetWrapper.isLoaded())
    val asset = assetWrapper.asset
    assertTrue(asset is MockAsset)
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
    assertEquals("additional", asset.additional)
    assertSame(assetManager, (assetWrapper as ManagedAsset).manager)
  }

  @Test
  fun `should schedule asset loading with AssetDescriptor`() {
    val assetWrapper = assetManager.loadAsset(assetDescriptor<MockAsset>("test"))
    assetManager.finishLoading()

    assertTrue(assetWrapper.isLoaded())
    val asset = assetWrapper.asset
    assertTrue(asset is MockAsset)
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
    assertSame(assetManager, (assetWrapper as ManagedAsset).manager)
  }

  @Test
  fun `should load on demand`() {
    val assetWrapper = assetManager.loadOnDemand<MockAsset>("test")

    assertFalse(assetWrapper.isLoaded())
    assetManager.finishLoading()
    // Loading was not supposed to be scheduled, should be still unloaded:
    assertFalse(assetWrapper.isLoaded())
    // Loaded on first asset getter call:
    val asset = assetWrapper.asset
    assertTrue(assetWrapper.isLoaded())
    assertTrue(asset is MockAsset)
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
    assertSame(assetManager, (assetWrapper as DelayedAsset).manager)
  }

  @Test
  fun `should load on demand with parameters`() {
    val assetWrapper = assetManager.loadOnDemand("test", MockParameter("additional"))

    assertFalse(assetWrapper.isLoaded())
    assetManager.finishLoading()
    // Loading was not supposed to be scheduled, should be still unloaded:
    assertFalse(assetWrapper.isLoaded())
    // Loaded on first asset getter call:
    val asset = assetWrapper.asset
    assertTrue(assetWrapper.isLoaded())
    assertTrue(asset is MockAsset)
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
    assertEquals("additional", asset.additional)
    assertSame(assetManager, (assetWrapper as DelayedAsset).manager)
  }

  @Test
  fun `should load on demand with descriptor`() {
    val assetWrapper = assetManager.loadOnDemand(assetDescriptor<MockAsset>("test"))

    assertFalse(assetWrapper.isLoaded())
    assetManager.finishLoading()
    // Loading was not supposed to be scheduled, should be still unloaded:
    assertFalse(assetWrapper.isLoaded())
    // Loaded on first asset getter call:
    val asset = assetWrapper.asset
    assertTrue(assetWrapper.isLoaded())
    assertTrue(asset is MockAsset)
    assertEquals(assetManager["test"], asset)
    assertEquals("test", asset.data)
    assertSame(assetManager, (assetWrapper as DelayedAsset).manager)
  }

  @Test
  fun `should extract loaded asset with explicit type from manager`() {
    assetManager.load<MockAsset>("test")
    assetManager.finishLoading()

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
    assetManager.finishLoading()
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
    assetManager.finishLoading()

    assetManager.unloadSafely("test")
    assetManager.unloadSafely("test")
    assetManager.unloadSafely("test")
    assertFalse(assetManager.isLoaded("test"))
  }

  @Test
  fun `should ignore exception due to prior disposal`() {
    assetManager.load<MockAsset>("test")
    assetManager.finishLoading()
    val mockAsset = assetManager.get<MockAsset>("test")
    mockAsset.dispose()
    assetManager.unloadSafely("test")
  }

  @Test
  fun `should unload asset handling exception`() {
    assetManager.load<MockAsset>("test")
    assetManager.finishLoading()
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
    assetManager.finishLoading()

    assertTrue(test.asset is MockAsset)
    assertEquals(assetManager["test"], test.asset)
    assertEquals("test", test.asset.data)
  }

  @Test
  fun `should delegate local variable to scheduled asset`() {
    val asset by assetManager.load<MockAsset>("test")
    assetManager.finishLoading()

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

  @Test
  fun `should load and unload members`() {
    class TestAssetGroup : AssetGroup(assetManager){
      val member1 by asset<MockAsset>("member1")
      val member2 by asset<MockAsset>("member2")
    }

    val group = TestAssetGroup()
    group.finishLoading()
    assertTrue(group.isLoaded())

    val mockAssets = listOf(group.member1, group.member2)
    group.unloadAll()
    assertTrue(!group.isLoaded())
    for (mockAsset in mockAssets)
      assertTrue(mockAsset.disposed)
  }

  @Test
  fun `should load by update`() {
    class TestAssetGroup : AssetGroup(assetManager){
      val member1 by asset<MockAsset>("member1")
      val member2 by asset<MockAsset>("member2")
      val member3 by asset<MockAsset>("member3")
    }
    val nonmember = assetManager.load<MockAsset>("nonmember")

    val group = TestAssetGroup()
    nonmember.load()
    while (true){
      if (group.update())
        break
    }
    assertTrue(group.isLoaded())
    assertTrue(listOf(group.member1, group.member2, group.member3).all { !it.disposed })
  }

  @Test
  fun `should catch exceptions`() {
    class TestAssetGroup : AssetGroup(assetManager){
      val member1 by asset<MockAsset>("member1")
      val member2 by asset<MockAsset>("member2")
    }

    val group = TestAssetGroup()
    with(group) {
      finishLoading()
      member1.dispose()
      member2.dispose()
    }

    var caught = 0
    group.unloadAllSafely { _, _ ->
      caught++
    }
    assertEquals(caught, 2)
  }

  @Test
  fun `should apply prefix`() {
    class TestAssetGroup : AssetGroup(assetManager, "prefix/") {
      val member1 = delayedAsset<MockAsset>("member1")
      val member2 = delayedAsset<MockAsset>("member2")
    }

    val group = TestAssetGroup().apply {
      loadAll()
      manager.finishLoading()
    }
    for (i in 1..2)
      assertTrue(group.manager.isLoaded("prefix/member$i"))
  }
}

/**
 * Creates an [AssetManager] with registered [MockAssetLoader].
 */
private fun managerWithMockAssetLoader() = AssetManager().apply {
  setLoader(MockAssetLoader(fileHandleResolver))
}

/**
 * Represents a mock-up asset. Implements [Disposable] for testing utility.
 * @param data path of the file.
 * @param additional optional string value passed with [MockParameter].
 */
class MockAsset(val data: String, val additional: String?) : Disposable {
  var disposed = false
  override fun dispose() {
    require(!disposed) { "Was already disposed!" } // Simulate behavior of some Gdx assets
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

  override fun loadSync(
    manager: AssetManager, fileName: String, file: FileHandle, parameter: MockParameter?
  ): MockAsset {
    val asset = MockAsset(file.path(), additional)
    additional = null
    return asset
  }

  override fun getDependencies(fileName: String?, file: FileHandle?, parameter: MockParameter?):
      Array<AssetDescriptor<Any>>? = null

  /** Allows to set [MockAsset.additional] via loader. Tests assets parameters API. */
  class MockParameter(val additional: String?) : AssetLoaderParameters<MockAsset>()
}
