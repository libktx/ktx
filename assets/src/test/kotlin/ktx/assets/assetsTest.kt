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
import ktx.assets.AssetsTest.MockAssetLoader.MockParameter
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests asset-related utilities.
 * @author MJ
 */
class AssetsTest {
  @Before
  fun mockFiles() {
    Gdx.files = MockFiles()
    // Clearing global AssetManager and adding MockAsset loader:
    Assets.manager.clear()
    Assets.manager.setLoader(MockAsset::class.java, MockAssetLoader(Assets.manager.fileHandleResolver))
  }

  /**
   * Spin-waits on [AssetManager.update].
   */
  private fun finishLoading() {
    while (!Assets.manager.update()) {
    }
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
  fun shouldCreateAssetDescriptorWithStringPath() {
    val descriptor = assetDescriptor<MockAsset>("mock.file")
    assertEquals(MockAsset::class.java, descriptor.type)
    assertEquals("mock.file", descriptor.fileName)

    val parameter = MockParameter("mock")
    val descriptorWithParams = assetDescriptor("mock.file", parameter)
    assertEquals(MockAsset::class.java, descriptorWithParams.type)
    assertEquals("mock.file", descriptorWithParams.fileName)
    assertEquals(parameter, descriptorWithParams.params)
  }

  @Test
  fun shouldCreateAssetDescriptorWithFileHandle() {
    val descriptor = assetDescriptor<MockAsset>(Gdx.files.internal("mock.file"))
    assertEquals(MockAsset::class.java, descriptor.type)
    assertEquals("mock.file", descriptor.file.path())

    val parameter = MockParameter("mock")
    val descriptorWithParams = assetDescriptor(Gdx.files.internal("mock.file"), parameter)
    assertEquals(MockAsset::class.java, descriptorWithParams.type)
    assertEquals("mock.file", descriptorWithParams.file.path())
    assertEquals(parameter, descriptorWithParams.params)
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
   * Represents a mock-up asset. Implements [Disposable] for testing utility.
   * @author MJ
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
   * @author MJ
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

    /**
     * Allows to set [MockAsset.additional].
     * @author MJ
     */
    class MockParameter(val additional: String?) : AssetLoaderParameters<MockAsset>()
  }
}