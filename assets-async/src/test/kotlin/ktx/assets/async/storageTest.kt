package ktx.assets.async

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Cubemap
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.Logger
import com.google.common.collect.Sets
import com.nhaarman.mockitokotlin2.*
import io.kotlintest.matchers.shouldThrow
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import ktx.assets.TextAssetLoader.TextAssetLoaderParameters
import ktx.async.*
import org.junit.AfterClass
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import java.lang.Integer.min
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect as ParticleEffect3D
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Tests [AssetStorage]: coroutines-based asset manager.
 *
 * This suite tests the behavior and logic of the [AssetStorage]. [AbstractAssetStorageLoadingTest]
 * and its extensions test whether [AssetStorage] can correctly load all default asset types.
 *
 * Implementation note: the tests use [runBlocking] to launch the coroutines for simplicity
 * of asserts. Normally [KtxAsync].launch is highly encouraged to build truly asynchronous applications.
 * Using [runBlocking] on the main rendering thread might lead to deadlocks, as the rendering thread
 * is necessary to load some assets (e.g. textures).
 *
 * In a similar manner, some [AssetDescriptor] instances are created manually. In an actual application,
 * using [AssetStorage.getAssetDescriptor] is a much easier way of obtaining [AssetDescriptor] instances.
 */
class AssetStorageTest : AsyncTest() {
  @get:Rule
  var testName = TestName()

  companion object {
    @JvmStatic
    @BeforeClass
    fun `load LibGDX statics`() {
      // Necessary for LibGDX asset loaders to work.
      LwjglNativesLoader.load()
      Gdx.graphics = mock()
      Gdx.gl20 = mock()
      Gdx.gl = Gdx.gl20
    }

    @JvmStatic
    @AfterClass
    fun `dispose of LibGDX statics`() {
      Gdx.graphics = null
      Gdx.audio = null
      Gdx.gl20 = null
      Gdx.gl = null
    }
  }

  /**
   * Allows to validate state of [LoadingProgress] without failing the test case.
   * Pass [warn] not to fail the test on progress mismatch.
   *
   * Progress is eventually consistent. It does not have to be up to date with the [AssetStorage] state.
   * Usually it will be and all tests would pass just fine, but there are these rare situations where
   * the asserts are evaluated before the progress is updated. That's why if such case is possible,
   * only a warning will be printed instead of failing the test.
   *
   * If the warnings are common, it might point to a bug within the progress updating.
   */
  private fun checkProgress(
    storage: AssetStorage,
    loaded: Int = 0, failed: Int = 0,
    total: Int = loaded + failed,
    warn: Boolean = false
  ) {
    if (warn) {
      val progress = storage.progress
      if (total != progress.total || loaded != progress.loaded || failed != progress.failed) {
        System.err.println("""
          Warning: mismatch in progress value in `${testName.methodName}`.
          Value  | Expected | Actual
          total  | ${"%8d".format(total)} | ${progress.total}
          loaded | ${"%8d".format(loaded)} | ${progress.loaded}
          failed | ${"%8d".format(failed)} | ${progress.failed}
          If this warning is repeated consistently, there might be a related bug in progress reporting.
        """.trimIndent())
      }
    } else {
      assertEquals(total, storage.progress.total)
      assertEquals(loaded, storage.progress.loaded)
      assertEquals(failed, storage.progress.failed)
    }
  }

  @Test
  fun `should throw exception when attempting to get unloaded asset`() {
    // Given:
    val storage = AssetStorage()

    // When:
    shouldThrow<MissingAssetException> {
      storage.get<String>("ktx/assets/async/string.txt")
    }

    // Then:
    checkProgress(storage, total = 0)
  }

  @Test
  fun `should return null when attempting to get unloaded asset or null`() {
    // Given:
    val storage = AssetStorage()

    // When:
    val asset = storage.getOrNull<String>("ktx/assets/async/string.txt")

    // Then:
    assertNull(asset)
    checkProgress(storage, total = 0)
  }

  @Test
  fun `should return deferred that throws exception when attempting to get unloaded asset asynchronously`() {
    // Given:
    val storage = AssetStorage()

    // When:
    val result = storage.getAsync<String>("ktx/assets/async/string.txt")

    // Then:
    shouldThrow<MissingAssetException> {
      runBlocking { result.await() }
    }
    checkProgress(storage, total = 0)
  }

  @Test
  fun `should throw exception when attempting to get unloaded asset with identifier`() {
    // Given:
    val storage = AssetStorage()
    val identifier = storage.getIdentifier<String>("ktx/assets/async/string.txt")

    // When:
    shouldThrow<MissingAssetException> {
      storage[identifier]
    }

    // Then:
    checkProgress(storage, total = 0)
  }

  @Test
  fun `should return null when attempting to get unloaded asset or null with identifier`() {
    // Given:
    val storage = AssetStorage()
    val identifier = storage.getIdentifier<String>("ktx/assets/async/string.txt")

    // When:
    val asset = storage.getOrNull(identifier)

    // Then:
    assertNull(asset)
    checkProgress(storage, total = 0)
  }

  @Test
  fun `should return deferred that throws exception when attempting to get unloaded asset asynchronously with identifier`() {
    // Given:
    val storage = AssetStorage()
    val identifier = storage.getIdentifier<String>("ktx/assets/async/string.txt")

    // When:
    val result = storage.getAsync(identifier)

    // Then:
    shouldThrow<MissingAssetException> {
      runBlocking { result.await() }
    }
    checkProgress(storage, total = 0)
  }

  @Test
  fun `should throw exception when attempting to get unloaded asset with descriptor`() {
    // Given:
    val storage = AssetStorage()
    val descriptor = storage.getAssetDescriptor<String>("ktx/assets/async/string.txt")

    // When:
    shouldThrow<MissingAssetException> {
      storage[descriptor]
    }

    // Then:
    checkProgress(storage, total = 0)
  }

  @Test
  fun `should return null when attempting to get unloaded asset or null with descriptor`() {
    // Given:
    val storage = AssetStorage()
    val descriptor = storage.getAssetDescriptor<String>("ktx/assets/async/string.txt")

    // When:
    val asset = storage.getOrNull(descriptor)

    // Then:
    assertNull(asset)
    checkProgress(storage, total = 0)
  }

  @Test
  fun `should return deferred that throws exception when attempting to get unloaded asset asynchronously with descriptor`() {
    // Given:
    val storage = AssetStorage()
    val descriptor = storage.getAssetDescriptor<String>("ktx/assets/async/string.txt")

    // When:
    val result = storage.getAsync(descriptor)

    // Then:
    shouldThrow<MissingAssetException> {
      runBlocking { result.await() }
    }
    checkProgress(storage, total = 0)
  }

  @Test
  fun `should obtain loaded asset with path`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"

    // When:
    runBlocking { storage.load<String>(path) }

    // Then:
    assertTrue(storage.contains<String>(path))
    assertTrue(storage.isLoaded<String>(path))
    assertEquals("Content.", storage.get<String>(path))
    assertEquals("Content.", storage.getOrNull<String>(path))
    assertEquals("Content.", runBlocking { storage.getAsync<String>(path).await() })
    assertEquals(emptyList<String>(), storage.getDependencies<String>(path))
    checkProgress(storage, loaded = 1, warn = true)
  }

  @Test
  fun `should obtain loaded asset with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val identifier = storage.getIdentifier<String>("ktx/assets/async/string.txt")

    // When:
    runBlocking { storage.loadAsync<String>(identifier.path).await() }

    // Then:
    assertTrue(identifier in storage)
    assertTrue(storage.isLoaded(identifier))
    assertEquals("Content.", storage[identifier])
    assertEquals("Content.", storage.getOrNull(identifier))
    assertEquals("Content.", runBlocking { storage.getAsync(identifier).await() })
    assertEquals(emptyList<String>(), storage.getDependencies(identifier))
    checkProgress(storage, loaded = 1, warn = true)
  }

  @Test
  fun `should obtain loaded asset with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = storage.getAssetDescriptor<String>("ktx/assets/async/string.txt")

    // When:
    storage.loadSync<String>(descriptor.fileName)

    // Then:
    assertTrue(descriptor in storage)
    assertTrue(storage.isLoaded(descriptor))
    assertEquals("Content.", storage[descriptor])
    assertEquals("Content.", storage.getOrNull(descriptor))
    assertEquals("Content.", runBlocking { storage.getAsync(descriptor).await() })
    assertEquals(emptyList<String>(), storage.getDependencies(descriptor))
    checkProgress(storage, loaded = 1, warn = true)
  }

  @Test
  fun `should unload assets with path`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    runBlocking { storage.load<String>(path) }

    // When:
    runBlocking { storage.unload<String>(path) }

    // Then:
    assertFalse(storage.isLoaded<String>(path))
    assertEquals(0, storage.getReferenceCount<String>(path))
    checkProgress(storage, total = 0, warn = true)
  }

  @Test
  fun `should unload assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    val descriptor = storage.getAssetDescriptor<String>(path)
    runBlocking { storage.loadAsync<String>(path).await() }

    // When:
    runBlocking { storage.unload(descriptor) }

    // Then:
    assertFalse(storage.isLoaded(descriptor))
    assertEquals(0, storage.getReferenceCount(descriptor))
    checkProgress(storage, total = 0, warn = true)
  }

  @Test
  fun `should unload assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    val identifier = storage.getIdentifier<String>(path)
    storage.loadSync<String>(path)

    // When:
    runBlocking { storage.unload(identifier) }

    // Then:
    assertFalse(storage.isLoaded(identifier))
    assertEquals(0, storage.getReferenceCount(identifier))
    checkProgress(storage, total = 0, warn = true)
  }

  @Test
  fun `should differentiate assets by path and type`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)

    // When:
    runBlocking { storage.add("path", "ASSET") }

    // Then:
    assertTrue(storage.contains<String>("path"))
    assertFalse(storage.contains<String>("different path"))
    assertFalse(storage.contains<Int>("path")) // Different type.
  }

  @Test
  fun `should point to the same asset when loading with path, descriptor and identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    val descriptor = storage.getAssetDescriptor<String>(path)
    val identifier = storage.getIdentifier<String>(path)

    // When:
    val viaPath = runBlocking { storage.load<String>(path) }
    val viaDescriptor = runBlocking { storage.load(descriptor) }
    val viaIdentifier = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<String>(path))
    assertSame(viaPath, viaDescriptor)
    assertSame(viaDescriptor, viaIdentifier)
    assertEquals(3, storage.getReferenceCount<String>(path))
    checkProgress(storage, loaded = 1, warn = true)
  }

  @Test
  fun `should point to the same asset when loading with path, descriptor and identifier asynchronously`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    val descriptor = storage.getAssetDescriptor<String>(path)
    val identifier = storage.getIdentifier<String>(path)
    val pathReference = storage.loadAsync<String>(path)
    val descriptorReference = storage.loadAsync(descriptor)
    val identifierReference = storage.loadAsync(identifier)

    // When:
    val viaPath = runBlocking { pathReference.await() }
    val viaDescriptor = runBlocking { descriptorReference.await() }
    val viaIdentifier = runBlocking { identifierReference.await() }

    // Then:
    assertTrue(storage.isLoaded<String>(path))
    assertSame(viaPath, viaDescriptor)
    assertSame(viaDescriptor, viaIdentifier)
    assertEquals(3, storage.getReferenceCount<String>(path))
    checkProgress(storage, loaded = 1, warn = true)
  }

  @Test
  fun `should point to the same asset when loading with path, descriptor and identifier synchronously`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    val descriptor = storage.getAssetDescriptor<String>(path)
    val identifier = storage.getIdentifier<String>(path)

    // When:
    val viaPath = storage.loadSync<String>(path)
    val viaDescriptor = storage.loadSync(descriptor)
    val viaIdentifier = storage.loadSync(identifier)

    // Then:
    assertTrue(storage.isLoaded<String>(path))
    assertSame(viaPath, viaDescriptor)
    assertSame(viaDescriptor, viaIdentifier)
    assertEquals(3, storage.getReferenceCount<String>(path))
    checkProgress(storage, loaded = 1, warn = true)
  }

  @Test
  fun `should manually add asset to storage`() {
    // Given:
    val storage = AssetStorage()
    val asset = Vector2(20f, 10f)
    val fakePath = "myVector"

    // When:
    runBlocking { storage.add(fakePath, asset) }

    // Then:
    assertTrue(storage.isLoaded<Vector2>(fakePath))
    assertSame(asset, storage.get<Vector2>(fakePath))
    assertEquals(1, storage.getReferenceCount<Vector2>(fakePath))
    checkProgress(storage, loaded = 1)
  }

  @Test
  fun `should manually add asset to storage with descriptor`() {
    // Given:
    val storage = AssetStorage()
    val asset = Vector2(20f, 10f)
    val fakePath = "myVector"
    val descriptor = storage.getAssetDescriptor<Vector2>(fakePath)

    // When:
    runBlocking { storage.add(descriptor, asset) }

    // Then:
    assertTrue(storage.isLoaded(descriptor))
    assertSame(asset, storage[descriptor])
    assertEquals(1, storage.getReferenceCount(descriptor))
    checkProgress(storage, loaded = 1)
  }

  @Test
  fun `should manually add asset to storage with identifier`() {
    // Given:
    val storage = AssetStorage()
    val asset = Vector2(20f, 10f)
    val fakePath = "myVector"
    val identifier = storage.getIdentifier<Vector2>(fakePath)

    // When:
    runBlocking { storage.add(identifier, asset) }

    // Then:
    assertTrue(storage.isLoaded(identifier))
    assertSame(asset, storage[identifier])
    assertEquals(1, storage.getReferenceCount(identifier))
    checkProgress(storage, loaded = 1)
  }

  @Test
  fun `should unload and dispose assets manually added to storage`() {
    // Given:
    val storage = AssetStorage()
    val asset = FakeAsset()
    val fakePath = "disposable"
    runBlocking { storage.add(fakePath, asset) }

    // When:
    val unloaded = runBlocking { storage.unload<FakeAsset>(fakePath) }

    // Then:
    assertTrue(unloaded)
    assertFalse(storage.isLoaded<FakeAsset>(fakePath))
    assertEquals(0, storage.getReferenceCount<FakeAsset>(fakePath))
    assertTrue(asset.isDisposed)
    checkProgress(storage, total = 0, warn = true)
  }

  @Test
  fun `should add asset after it was unloaded`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"
    val asset = mock<Texture>()
    runBlocking { storage.load<Texture>(path) }
    runBlocking { storage.unload<Texture>(path) }

    // When:
    runBlocking { storage.add(path, asset) }

    // Then:
    assertTrue(storage.isLoaded<Texture>(path))
    assertSame(asset, storage.get<Texture>(path))
  }

  @Test
  fun `should allow to add asset with different type than loaded`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"
    val asset = mock<Pixmap>()
    runBlocking { storage.load<Texture>(path) }

    // When:
    runBlocking { storage.add(path, asset) }

    // Then:
    assertTrue(storage.isLoaded<Texture>(path))
    assertTrue(storage.isLoaded<Pixmap>(path))
    assertSame(asset, storage.get<Pixmap>(path))

    storage.dispose()
  }

  @Test
  fun `should allow to load multiple assets with different type and same path`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"

    // When:
    runBlocking {
      storage.load<Texture>(path)
      storage.load<Pixmap>(path)
    }

    // Then:
    assertTrue(storage.isLoaded<Texture>(path))
    assertTrue(storage.isLoaded<Pixmap>(path))
    assertEquals(1, storage.getReferenceCount<Texture>(path))
    assertEquals(1, storage.getReferenceCount<Pixmap>(path))
    assertNotSame(storage.get<Texture>(path), storage.get<Pixmap>(path))
    checkProgress(storage, loaded = 2, warn = true)

    storage.dispose()
  }

  @Test
  fun `should support loading assets in parallel`() {
    // Given:
    val storage = AssetStorage(
      fileResolver = ClasspathFileHandleResolver(),
      asyncContext = newAsyncContext(2)
    )
    val firstPath = "ktx/assets/async/texture.png"
    val secondPath = "ktx/assets/async/model.obj"
    val scheduler = newAsyncContext(2)
    val tasksReference = CompletableFuture<List<Deferred<*>>>()

    // When:
    KtxAsync.launch {
      val first = async(scheduler) { storage.load<Texture>(firstPath) }
      val second = async(scheduler) { storage.load<Model>(secondPath) }
      tasksReference.complete(listOf(first, second))
    }

    // Then:
    val tasks = tasksReference.join()
    runBlocking { tasks.joinAll() }
    assertTrue(storage.isLoaded<Texture>(firstPath))
    assertTrue(storage.isLoaded<Model>(secondPath))
    assertSame(tasks[0].asCompletableFuture().join(), storage.get<Texture>(firstPath))
    assertSame(tasks[1].asCompletableFuture().join(), storage.get<Model>(secondPath))
    checkProgress(storage, loaded = 2, warn = true)

    storage.dispose()
  }

  @Test
  fun `should fail to add asset that was already loaded`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    runBlocking { storage.load<String>(path) }

    // When:
    shouldThrow<AlreadyLoadedAssetException> {
      runBlocking {
        storage.add(path, "ASSET")
      }
    }

    // Then:
    assertEquals(1, storage.getReferenceCount<String>(path))
  }

  @Test
  fun `should fail to add asset that was already added`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    runBlocking { storage.add(path, "ASSET") }

    // When:
    shouldThrow<AlreadyLoadedAssetException> {
      runBlocking {
        // Even though the asset is the same, `add` should work only once.
        // This is because finding all dependencies of an asset is tricky
        // if it is both loaded and added. To keep the dependencies reference
        // counts in check, we're only allowing to add an asset once.
        storage.add(path, "ASSET")
      }
    }

    // Then:
    assertEquals(1, storage.getReferenceCount<String>(path))
  }

  @Test
  fun `should increase references count and return the same asset when trying to load asset with same path`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    val elements = IdentityHashMap<String, Boolean>()

    // When:
    runBlocking {
      repeat(3) {
        val asset = storage.load<String>(path)
        elements[asset] = true
      }
    }

    // Then:
    assertEquals(3, storage.getReferenceCount<String>(path))
    assertEquals(1, elements.size)
  }

  @Test
  fun `should fail to load asset with missing loader`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"

    // When:
    shouldThrow<MissingLoaderException> {
      runBlocking { storage.load<Vector2>(path) }
    }

    // Then:
    assertFalse(storage.contains<Vector2>(path))
    checkProgress(storage, total = 0)
  }

  @Test
  fun `should register default loaders`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = true)

    // Expect:
    assertNotNull(storage.getLoader<String>())
    assertNotNull(storage.getLoader<BitmapFont>())
    assertNotNull(storage.getLoader<Music>())
    assertNotNull(storage.getLoader<Pixmap>())
    assertNotNull(storage.getLoader<Sound>())
    assertNotNull(storage.getLoader<TextureAtlas>())
    assertNotNull(storage.getLoader<Texture>())
    assertNotNull(storage.getLoader<Skin>())
    assertNotNull(storage.getLoader<ParticleEffect>())
    assertNotNull(storage.getLoader<ParticleEffect3D>())
    assertNotNull(storage.getLoader<I18NBundle>())
    assertNotNull(storage.getLoader<Model>(path = ".obj"))
    assertNotNull(storage.getLoader<Model>(path = ".g3dj"))
    assertNotNull(storage.getLoader<Model>(path = ".g3db"))
    assertNotNull(storage.getLoader<ShaderProgram>())
    assertNotNull(storage.getLoader<Cubemap>())
  }

  @Test
  fun `should not register default loaders`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)

    // Expect:
    assertNull(storage.getLoader<String>())
    assertNull(storage.getLoader<BitmapFont>())
    assertNull(storage.getLoader<Music>())
    assertNull(storage.getLoader<Pixmap>())
    assertNull(storage.getLoader<Sound>())
    assertNull(storage.getLoader<TextureAtlas>())
    assertNull(storage.getLoader<Texture>())
    assertNull(storage.getLoader<Skin>())
    assertNull(storage.getLoader<ParticleEffect>())
    assertNull(storage.getLoader<ParticleEffect3D>())
    assertNull(storage.getLoader<I18NBundle>())
    assertNull(storage.getLoader<Model>(path = ".obj"))
    assertNull(storage.getLoader<Model>(path = ".g3dj"))
    assertNull(storage.getLoader<Model>(path = ".g3db"))
    assertNull(storage.getLoader<ShaderProgram>())
    assertNull(storage.getLoader<Cubemap>())
  }

  @Test
  fun `should increase references counts of dependencies when loading asset with same path`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.json"
    val dependencies = arrayOf(
      storage.getIdentifier<TextureAtlas>("ktx/assets/async/skin.atlas"),
      storage.getIdentifier<Texture>("ktx/assets/async/texture.png")
    )
    val loadedAssets = IdentityHashMap<Skin, Boolean>()

    // When:
    runBlocking {
      repeat(3) {
        val asset = storage.load<Skin>(path)
        loadedAssets[asset] = true
      }
    }

    // Then:
    assertEquals(3, storage.getReferenceCount<Skin>(path))
    dependencies.forEach {
      assertEquals(3, storage.getReferenceCount(it))
    }
    assertEquals(1, loadedAssets.size)
    checkProgress(storage, loaded = 3, warn = true)
  }

  @Test
  fun `should increase references counts of dependencies when loading asset with same descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = storage.getAssetDescriptor<Skin>("ktx/assets/async/skin.json")
    val dependencies = arrayOf(
      storage.getIdentifier<TextureAtlas>("ktx/assets/async/skin.atlas"),
      storage.getIdentifier<Texture>("ktx/assets/async/texture.png")
    )
    val loadedAssets = IdentityHashMap<Skin, Boolean>()

    // When:
    runBlocking {
      repeat(3) {
        val asset = storage.loadAsync(descriptor).await()
        loadedAssets[asset] = true
      }
    }

    // Then:
    assertEquals(3, storage.getReferenceCount(descriptor))
    dependencies.forEach {
      assertEquals(3, storage.getReferenceCount(it))
    }
    assertEquals(1, loadedAssets.size)
    checkProgress(storage, loaded = 3, warn = true)

    storage.dispose()
  }

  @Test
  fun `should increase references counts of dependencies when loading asset with same identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val identifier = storage.getIdentifier<Skin>("ktx/assets/async/skin.json")
    val dependencies = arrayOf(
      storage.getIdentifier<TextureAtlas>("ktx/assets/async/skin.atlas"),
      storage.getIdentifier<Texture>("ktx/assets/async/texture.png")
    )
    val loadedAssets = IdentityHashMap<Skin, Boolean>()

    // When:
    repeat(3) {
      val asset = storage.loadSync(identifier)
      loadedAssets[asset] = true
    }

    // Then:
    assertEquals(3, storage.getReferenceCount(identifier))
    dependencies.forEach {
      assertEquals(3, storage.getReferenceCount(it))
    }
    assertEquals(1, loadedAssets.size)
    checkProgress(storage, loaded = 3, warn = true)

    storage.dispose()
  }

  @Test
  fun `should not unload dependencies that still referenced by other assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.json"
    val dependency = "ktx/assets/async/skin.atlas"
    val nestedDependency = "ktx/assets/async/texture.png"
    runBlocking {
      storage.load<Skin>(path)
      storage.load<TextureAtlas>(dependency)
    }

    // When:
    runBlocking { storage.unload<Skin>(path) }

    // Then:
    assertEquals(0, storage.getReferenceCount<Skin>(path))
    assertFalse(storage.isLoaded<Skin>(path))
    assertEquals(1, storage.getReferenceCount<TextureAtlas>(dependency))
    assertTrue(storage.isLoaded<TextureAtlas>(dependency))
    assertEquals(1, storage.getReferenceCount<Texture>(nestedDependency))
    assertTrue(storage.isLoaded<Texture>(nestedDependency))
    checkProgress(storage, loaded = 2, warn = true)

    storage.dispose()
  }

  @Test
  fun `should support concurrent loading`() {
    // Given:
    val schedulers = newAsyncContext(threads = 16)
    val loaders = newAsyncContext(threads = 4)
    val storage = AssetStorage(
      fileResolver = ClasspathFileHandleResolver(),
      asyncContext = loaders
    )
    val path = "com/badlogic/gdx/utils/arial-15.fnt"
    val dependency = "com/badlogic/gdx/utils/arial-15.png"

    // When:
    val assets = (1..100).map {
      val result = CompletableDeferred<BitmapFont>()
      KtxAsync.launch(schedulers) {
        val asset = storage.load<BitmapFont>(path)
        result.complete(asset)
      }
      result
    }

    // Then:
    runBlocking { assets.joinAll() }
    assertTrue(storage.isLoaded<BitmapFont>(path))
    assertEquals(100, storage.getReferenceCount<BitmapFont>(path))
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertEquals(100, storage.getReferenceCount<Texture>(dependency))
    assertEquals(1, assets.map { it.asCompletableFuture().join() }.toSet().size)
    checkProgress(storage, loaded = 2)

    storage.dispose()
  }

  /**
   * Allows to test concurrent loading and unloading.
   *
   * During concurrent unloading, some assets are expected to be requested for unloading
   * before they are fully loaded, resulting in [UnloadedAssetException] caught by the
   * coroutines waiting for or loading the asset. This method allows to ignore these exceptions.
   */
  private suspend inline fun <reified T : Any> loadIgnoringUnloadException(storage: AssetStorage, path: String) {
    try {
      storage.load<T>(path)
    } catch (exception: UnloadedAssetException) {
      // Expected.
    }
  }

  @Test
  fun `should support concurrent loading and unloading`() {
    // Given:
    val schedulers = newAsyncContext(threads = 16)
    val loaders = newAsyncContext(threads = 4)
    val storage = AssetStorage(
      fileResolver = ClasspathFileHandleResolver(),
      asyncContext = loaders
    )
    val path = "ktx/assets/async/string.txt"

    // When: spawning 100 coroutines that load and unload the asset, 1 of which loads it 2 times:
    val assets = (1..100).map { id ->
      val result = CompletableDeferred<Boolean>()
      KtxAsync.launch(schedulers) {
        loadIgnoringUnloadException<String>(storage, path)
        storage.unload<String>(path)
        if (id == 99) {
          // Loading 1 additional asset:
          loadIgnoringUnloadException<String>(storage, path)
        }
        result.complete(true)
      }
      result
    }

    // Then:
    runBlocking { assets.joinAll() }
    assertTrue(storage.isLoaded<String>(path))
    assertEquals(1, storage.getReferenceCount<String>(path))
    assertEquals("Content.", storage.get<String>(path))
    checkProgress(storage, loaded = 1, warn = true)

    storage.dispose()
  }

  @Test
  fun `should support concurrent loading and unloading with dependencies`() {
    // Given:
    val schedulers = newAsyncContext(threads = 16)
    val loaders = newAsyncContext(threads = 4)
    val storage = AssetStorage(
      fileResolver = ClasspathFileHandleResolver(),
      asyncContext = loaders
    )
    val path = "com/badlogic/gdx/utils/arial-15.fnt"
    val dependency = "com/badlogic/gdx/utils/arial-15.png"

    // When: spawning 100 coroutines that load and unload the asset, 1 of which loads it 2 times:
    val assets = (1..100).map { id ->
      val result = CompletableDeferred<Boolean>()
      KtxAsync.launch(schedulers) {
        loadIgnoringUnloadException<BitmapFont>(storage, path)
        storage.unload<BitmapFont>(path)
        if (id == 1) {
          // Loading 1 additional asset:
          loadIgnoringUnloadException<BitmapFont>(storage, path)
        }
        result.complete(true)
      }
      result
    }

    // Then:
    runBlocking { assets.joinAll() }
    assertTrue(storage.isLoaded<BitmapFont>(path))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(path))
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertEquals(1, storage.getReferenceCount<Texture>(dependency))
    assertSame(
      storage.get<BitmapFont>(path).region.texture,
      storage.get<Texture>(dependency)
    )
    checkProgress(storage, loaded = 2, warn = true)

    storage.dispose()
  }

  @Test
  fun `should support concurrent loading and unloading with deeply nested dependencies`() {
    // Given:
    val schedulers = newAsyncContext(threads = 16)
    val loaders = newAsyncContext(threads = 4)
    val storage = AssetStorage(
      fileResolver = ClasspathFileHandleResolver(),
      asyncContext = loaders
    )
    val path = "ktx/assets/async/skin.json"
    val dependency = "ktx/assets/async/skin.atlas"
    val nestedDependency = "ktx/assets/async/texture.png"

    // When: spawning 100 coroutines that load and unload the asset, 1 of which loads it 2 times:
    val assets = (1..100).map { id ->
      val result = CompletableDeferred<Boolean>()
      KtxAsync.launch(schedulers) {
        loadIgnoringUnloadException<Skin>(storage, path)
        storage.unload<Skin>(path)
        if (id == 1) {
          loadIgnoringUnloadException<Skin>(storage, path)
        }
        result.complete(true)
      }
      result
    }

    // Then:
    runBlocking { assets.joinAll() }
    assertTrue(storage.isLoaded<Skin>(path))
    assertEquals(1, storage.getReferenceCount<Skin>(path))
    assertTrue(storage.isLoaded<TextureAtlas>(dependency))
    assertEquals(1, storage.getReferenceCount<TextureAtlas>(dependency))
    assertTrue(storage.isLoaded<Texture>(nestedDependency))
    assertEquals(1, storage.getReferenceCount<Texture>(nestedDependency))
    val skin = storage.get<Skin>(path)
    val atlas = storage.get<TextureAtlas>(dependency)
    val texture = storage.get<Texture>(nestedDependency)
    assertSame(skin.atlas, atlas)
    assertSame(atlas.textures.first(), texture)
    checkProgress(storage, loaded = 3, warn = true)

    storage.dispose()
  }

  @Test
  fun `should handle stress test`() {
    // Given:
    val schedulers = newAsyncContext(threads = 16)
    val loaders = newAsyncContext(threads = 4)
    val storage = AssetStorage(
      fileResolver = ClasspathFileHandleResolver(),
      asyncContext = loaders
    )
    val path = "ktx/assets/async/skin.json"
    val dependency = "ktx/assets/async/skin.atlas"
    val nestedDependency = "ktx/assets/async/texture.png"
    val loads = AtomicInteger()
    val unloads = AtomicInteger()

    // When: spawning 1000 coroutines that randomly load or unload the asset and try to access it:
    val assets = (1..1000).map {
      val result = CompletableDeferred<Boolean>()
      KtxAsync.launch(schedulers) {
        repeat(ThreadLocalRandom.current().nextInt(50)) { skipFrame() }
        if (ThreadLocalRandom.current().nextFloat() < 0.4f) {
          // Some unloads are expected to miss and loads are longer,
          // so there's a lower probability for load.
          loads.incrementAndGet()
          loadIgnoringUnloadException<Skin>(storage, path)
        } else {
          val unloaded = storage.unload<Skin>(path)
          if (unloaded) unloads.incrementAndGet()
        }
        try {
          // Concurrent access:
          storage.getOrNull<Skin>(path)
        } catch (expected: UnloadedAssetException) {
          // Assets can be unloaded asynchronously. This is OK.
        }
        result.complete(true)
      }
      result
    }

    // Then:
    runBlocking { assets.joinAll() }
    val expectedReferences = max(0, loads.get() - unloads.get())
    assertEquals(expectedReferences, storage.getReferenceCount<Skin>(path))
    assertEquals(expectedReferences, storage.getReferenceCount<TextureAtlas>(dependency))
    assertEquals(expectedReferences, storage.getReferenceCount<Texture>(nestedDependency))
    // Either the skin is unloaded or there are 3 assets - skin, atlas, texture:
    val assetsCount = min(1, expectedReferences) * 3
    checkProgress(storage, loaded = assetsCount, warn = true)

    storage.dispose()
  }

  @Test
  fun `should register asset loader`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val loader = mock<SynchronousLoader<Int>>()

    // When:
    storage.setLoader { loader }

    // Then:
    assertSame(loader, storage.getLoader<Int>())

    // Implementation note: normally you create a new instance in the setLoader lambda,
    // this is just for verification.
  }

  @Test
  fun `should register asset loader with suffix`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val loader = mock<SynchronousLoader<Int>>()

    // When:
    storage.setLoader(suffix = ".txt") { loader }

    // Then:
    assertSame(loader, storage.getLoader<Int>("file.txt"))
    assertNull(storage.getLoader<Int>("file.md"))
  }

  @Test
  fun `should return null if asset loader is not available`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)

    // Expect:
    assertNull(storage.getLoader<Int>())
  }

  @Test
  fun `should reject invalid asset loader implementations`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)

    // When: loader does not extend Synchronous/AsynchronousAssetLoader:
    val invalidLoader = mock<Loader<Int>>()

    // Then:
    shouldThrow<InvalidLoaderException> {
      storage.setLoader { invalidLoader }
    }
  }

  @Test
  fun `should dispose of all assets`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val paths = (1..5).map { storage.getIdentifier<FakeAsset>(it.toString()) }
    val assets = paths.map { it to FakeAsset() }
      .onEach { (identifier, asset) -> runBlocking { storage.add(identifier, asset) } }
      .map { (_, asset) -> asset }

    // When:
    storage.dispose()

    // Then:
    assertTrue(assets.all { it.isDisposed })
    assertTrue(paths.all { it !in storage })
    checkProgress(storage, total = 0)
  }

  @Test
  fun `should dispose of all assets with optional error handling`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val paths = (1..5).map { storage.getIdentifier<FakeAsset>(it.toString()) }
    val assets = paths.map { it to FakeAsset() }
      .onEach { (identifier, asset) -> runBlocking { storage.add(identifier, asset) } }
      .map { (_, asset) -> asset }

    // When:
    runBlocking {
      storage.dispose { identifier, cause ->
        fail("Unexpected exception when unloading $identifier: $cause")
        throw cause
      }
    }

    // Then:
    assertTrue(assets.all { it.isDisposed })
    assertTrue(paths.all { it !in storage })
  }

  @Test
  fun `should catch errors during disposing with handler`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val validAsset = mock<Disposable>()
    val brokenAsset = mock<Disposable> {
      on(it.dispose()) doThrow GdxRuntimeException("Expected.")
    }
    runBlocking {
      storage.add("broken", brokenAsset)
      storage.add("valid", validAsset)
    }

    // When:
    runBlocking {
      storage.dispose { identifier, error ->
        assertEquals(storage.getIdentifier<Disposable>("broken"), identifier)
        assertEquals("Expected.", error.message)
        assertTrue(error is GdxRuntimeException)
      }
    }

    // Then:
    verify(brokenAsset).dispose()
    verify(validAsset).dispose()
  }

  @Test
  fun `should log errors during disposing`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val logger = mock<Logger>()
    storage.logger = logger
    val validAsset = mock<Disposable>()
    val exception = GdxRuntimeException("Expected.")
    val brokenAsset = mock<Disposable> {
      on(it.dispose()) doThrow exception
    }
    runBlocking {
      storage.add("broken", brokenAsset)
      storage.add("valid", validAsset)
    }

    // When:
    storage.dispose()

    // Then:
    verify(brokenAsset).dispose()
    verify(validAsset).dispose()
    verify(logger).error(any(), eq(exception))
  }

  @Test
  fun `should create AssetDescriptor`() {
    // When:
    val storage = AssetStorage(useDefaultLoaders = false)
    val descriptor = storage.getAssetDescriptor<String>("ktx/assets/async/string.txt")

    // Then:
    assertEquals("ktx/assets/async/string.txt", descriptor.fileName)
    assertEquals("ktx/assets/async/string.txt", descriptor.file.path())
    assertEquals(String::class.java, descriptor.type)
    assertNull(descriptor.params)
  }

  @Test
  fun `should create AssetDescriptor with loading parameters`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val parameters = mock<AssetLoaderParameters<String>>()

    // When:
    val descriptor = storage.getAssetDescriptor("ktx/assets/async/string.txt", parameters)

    // Then:
    assertEquals("ktx/assets/async/string.txt", descriptor.fileName)
    assertEquals("ktx/assets/async/string.txt", descriptor.file.path())
    assertEquals(String::class.java, descriptor.type)
    assertSame(parameters, descriptor.params)
  }

  @Test
  fun `should create AssetDescriptor with a custom file`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val file = mock<FileHandle>()

    // When:
    val descriptor = storage.getAssetDescriptor<String>("ktx/assets/async/string.txt", fileHandle = file)

    // Then:
    assertEquals("ktx/assets/async/string.txt", descriptor.fileName)
    assertEquals(String::class.java, descriptor.type)
    assertSame(file, descriptor.file)
  }

  @Test
  fun `should create Identifier`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val path = "my\\file.png"

    // When:
    val identifier = storage.getIdentifier<Vector2>(path)

    // Then: should normalize path and extract reified type:
    assertEquals("my/file.png", identifier.path)
    assertSame(Vector2::class.java, identifier.type)
  }

  @Test
  fun `should normalize file paths`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val paths = mapOf(
      "path.txt" to "path.txt",
      "dir/path.txt" to "dir/path.txt",
      "\\path.txt" to "/path.txt",
      "dir\\path.txt" to "dir/path.txt",
      "home\\dir\\path.txt" to "home/dir/path.txt",
      "\\home\\dir\\dir\\" to "/home/dir/dir/"
    )

    paths.forEach { (original, expected) ->
      // When:
      val normalized = with(storage) { original.normalizePath() }

      // Then: Should match AssetDescriptor logic.
      assertEquals(expected, normalized)
      assertEquals(AssetDescriptor(expected, String::class.java).fileName, normalized)
    }
  }

  @Test
  fun `should convert AssetDescriptor to Identifier`() {
    // Given:
    val assetDescriptor = AssetDescriptor("file.path", String::class.java, TextAssetLoaderParameters("UTF-8"))

    // When:
    val identifier = assetDescriptor.toIdentifier()

    // Then: should copy path and class without loading parameters:
    assertEquals(assetDescriptor.fileName, identifier.path)
    assertEquals(assetDescriptor.type, identifier.type)
  }

  @Test
  fun `should convert Identifier to AssetDescriptor`() {
    // Given:
    val identifier = Identifier("file.path", String::class.java)

    // When:
    val assetDescriptor = identifier.toAssetDescriptor()

    // Then:
    assertEquals("file.path", assetDescriptor.fileName)
    assertSame(String::class.java, assetDescriptor.type)
  }

  @Test
  fun `should convert Identifier to AssetDescriptor with loading parameters`() {
    // Given:
    val identifier = Identifier("file.path", String::class.java)
    val parameters = mock<AssetLoaderParameters<String>>()

    // When:
    val assetDescriptor = identifier.toAssetDescriptor(parameters)

    // Then:
    assertEquals("file.path", assetDescriptor.fileName)
    assertSame(String::class.java, assetDescriptor.type)
    assertSame(parameters, assetDescriptor.params)
  }

  @Test
  fun `should convert Identifier to AssetDescriptor with a custom file`() {
    // Given:
    val identifier = Identifier("file.path", String::class.java)
    val file = mock<FileHandle>()

    // When:
    val assetDescriptor = identifier.toAssetDescriptor(fileHandle = file)

    // Then:
    assertEquals("file.path", assetDescriptor.fileName)
    assertSame(String::class.java, assetDescriptor.type)
    assertSame(file, assetDescriptor.file)
  }

  @Test
  fun `should invoke loaded callback on rendering thread to match AssetManager behavior`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    val callbackFinished = CompletableFuture<Boolean>()
    val callbackExecutions = AtomicInteger()
    var callbackExecutedOnRenderingThread = false
    lateinit var callbackManager: AssetManager
    var callbackPath = ""
    var callbackType = Any::class.java
    val parameters = TextAssetLoaderParameters().apply {
      loadedCallback = AssetLoaderParameters.LoadedCallback { assetManager, fileName, type ->
        callbackExecutions.incrementAndGet()
        callbackExecutedOnRenderingThread = KtxAsync.isOnRenderingThread()
        callbackManager = assetManager
        callbackPath = fileName
        callbackType = type
        callbackFinished.complete(true)
      }
    }

    // When:
    runBlocking { storage.load(path, parameters) }

    // Then:
    callbackFinished.join()
    assertEquals(1, callbackExecutions.get())
    assertTrue(callbackExecutedOnRenderingThread)
    assertTrue(callbackManager is AssetManagerWrapper)
    assertSame(storage, (callbackManager as AssetManagerWrapper).assetStorage)
    assertEquals(path, callbackPath)
    assertSame(String::class.java, callbackType)
  }

  @Test
  fun `should log exceptions thrown by loading callbacks`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    val loggingFinished = CompletableFuture<Boolean>()
    val exception = IllegalStateException("Expected.")
    val logger = mock<Logger> {
      on(it.error(any(), any())) doAnswer { loggingFinished.complete(true); Unit }
    }
    storage.logger = logger
    val parameters = TextAssetLoaderParameters().apply {
      loadedCallback = AssetLoaderParameters.LoadedCallback { _, _, _ ->
        throw exception
      }
    }

    // When:
    runBlocking { storage.load(path, parameters) }

    // Then: asset should still be loaded, but the callback exception must be logged:
    loggingFinished.join()
    assertTrue(storage.isLoaded<String>(path))
    assertEquals("Content.", storage.get<String>(path))
    verify(logger).error(any(), eq(exception))
  }

  @Test
  fun `should not block the main rendering thread when loading assets synchronously`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"

    // When:
    val asset = runBlocking(KtxAsync.coroutineContext) {
      storage.loadSync<String>(path)
    }

    // Then:
    assertEquals("Content.", asset)
    assertTrue(storage.isLoaded<String>(path))
  }

  @Test
  fun `should not block the main rendering thread when loading assets with dependencies synchronously`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.json"
    val dependencies = arrayOf(
      storage.getIdentifier<TextureAtlas>("ktx/assets/async/skin.atlas"),
      storage.getIdentifier<Texture>("ktx/assets/async/texture.png")
    )

    // When:
    val asset = runBlocking(KtxAsync.coroutineContext) {
      storage.loadSync<Skin>(path)
    }

    // Then:
    assertTrue(storage.isLoaded<Skin>(path))
    dependencies.forEach {
      assertTrue(storage.isLoaded(it))
    }
    assertSame(asset.atlas, storage[dependencies[0]])
  }

  /** For [Disposable.dispose] interface testing and loaders testing. */
  class FakeAsset : Disposable {
    val disposingFinished = CompletableFuture<Boolean>()
    var isDisposed: Boolean = false
    override fun dispose() {
      isDisposed = true
      disposingFinished.complete(true)
    }
  }

  /** For loaders testing. */
  class FakeAsyncLoader(
    private val onAsync: (assetManager: AssetManager) -> Unit,
    private val onSync: (asset: FakeAsset) -> Unit,
    private val dependencies: GdxArray<AssetDescriptor<*>> = GdxArray.with()
  ) : AsynchronousAssetLoader<FakeAsset, FakeParameters>(ClasspathFileHandleResolver()) {
    override fun loadAsync(
      manager: AssetManager, fileName: String?, file: FileHandle?, parameter: FakeParameters?
    ) {
      onAsync(manager)
    }

    override fun loadSync(
      manager: AssetManager, fileName: String?, file: FileHandle?, parameter: FakeParameters?
    ): FakeAsset = FakeAsset().also(onSync)

    @Suppress("UNCHECKED_CAST")
    override fun getDependencies(
      fileName: String?, file: FileHandle?, parameter: FakeParameters?
    ): GdxArray<AssetDescriptor<Any>> = dependencies as GdxArray<AssetDescriptor<Any>>
  }

  /** For loaders testing. */
  open class FakeSyncLoader(
    private val onLoad: (asset: FakeAsset) -> Unit,
    private val dependencies: GdxArray<AssetDescriptor<*>> = GdxArray.with()
  ) : SynchronousAssetLoader<FakeAsset, FakeParameters>(ClasspathFileHandleResolver()) {
    @Suppress("UNCHECKED_CAST")
    override fun getDependencies(
      fileName: String?, file: FileHandle?, parameter: FakeParameters?
    ): GdxArray<AssetDescriptor<Any>> = dependencies as GdxArray<AssetDescriptor<Any>>

    override fun load(
      assetManager: AssetManager, fileName: String?, file: FileHandle?, parameter: FakeParameters?
    ): FakeAsset = FakeAsset().also(onLoad)
  }

  /** For loaders testing. */
  class FakeParameters : AssetLoaderParameters<FakeAsset>()

  @Test
  fun `should execute asynchronous loading on specified context and synchronous loading on rendering thread`() {
    // Given:
    val asyncContext = newSingleThreadAsyncContext(threadName = "CustomThreadName")
    val asyncThread = getExecutionThread(asyncContext.executor)
    val isAsyncThread = CompletableFuture<Boolean>()
    val isRenderingThread = CompletableFuture<Boolean>()
    val isRenderingThreadDuringAsync = CompletableFuture<Boolean>()
    val loader = FakeAsyncLoader(
      onSync = { isRenderingThread.complete(KtxAsync.isOnRenderingThread()) },
      onAsync = {
        isAsyncThread.complete(asyncThread === Thread.currentThread())
        isRenderingThreadDuringAsync.complete(KtxAsync.isOnRenderingThread())
      }
    )
    val storage = AssetStorage(asyncContext = asyncContext, useDefaultLoaders = false)
    storage.setLoader { loader }

    // When:
    runBlocking { storage.load<FakeAsset>("fake.path") }

    // Then:
    assertTrue(isRenderingThread.getNow(false))
    assertTrue(isAsyncThread.getNow(false))
    assertFalse(isRenderingThreadDuringAsync.getNow(true))
  }

  @Test
  fun `should load assets on rendering thread with synchronous loading `() {
    // Given:
    val isRenderingThread = CompletableFuture<Boolean>()
    val loader = FakeSyncLoader(
      onLoad = { isRenderingThread.complete(KtxAsync.isOnRenderingThread()) }
    )
    val storage = AssetStorage(useDefaultLoaders = false)
    storage.setLoader { loader }

    // When:
    storage.loadSync<FakeAsset>("fake.path")

    // Then:
    assertTrue(isRenderingThread.getNow(false))
  }

  @Test
  fun `should load assets synchronously on rendering thread with synchronous loader`() {
    // Given:
    val isRenderingThread = CompletableFuture<Boolean>()
    val loader = FakeSyncLoader(
      onLoad = { isRenderingThread.complete(KtxAsync.isOnRenderingThread()) }
    )
    val storage = AssetStorage(useDefaultLoaders = false)
    storage.setLoader { loader }

    // When:
    storage.loadSync<FakeAsset>("fake.path")

    // Then:
    assertTrue(isRenderingThread.getNow(false))
  }

  @Test
  fun `should load assets synchronously on rendering thread with asynchronous loader`() {
    // Given:
    val isRenderingThreadDuringAsync = CompletableFuture<Boolean>()
    val isRenderingThreadDuringSync = CompletableFuture<Boolean>()
    val loader = FakeAsyncLoader(
      onAsync = { isRenderingThreadDuringAsync.complete(KtxAsync.isOnRenderingThread()) },
      onSync = { isRenderingThreadDuringSync.complete(KtxAsync.isOnRenderingThread()) }
    )
    val storage = AssetStorage(useDefaultLoaders = false)
    storage.setLoader { loader }

    // When:
    storage.loadSync<FakeAsset>("fake.path")

    // Then: entire synchronous loading should be executed on the rendering thread:
    assertTrue(isRenderingThreadDuringAsync.getNow(false))
    assertTrue(isRenderingThreadDuringSync.getNow(false))
  }

  @Test
  fun `should handle loading exceptions`() {
    // Given:
    val loader = FakeSyncLoader(
      onLoad = { throw IllegalStateException("Expected.") }
    )
    val storage = AssetStorage(useDefaultLoaders = false)
    storage.setLoader { loader }
    val path = "fake path"

    // When:
    shouldThrow<AssetLoadingException> {
      runBlocking { storage.load<FakeAsset>(path) }
    }

    // Then: asset should still be in storage, but rethrowing original exception:
    assertTrue(storage.contains<FakeAsset>(path))
    assertEquals(1, storage.getReferenceCount<FakeAsset>(path))
    shouldThrow<AssetLoadingException> {
      storage.get<FakeAsset>(path)
    }
    shouldThrow<AssetLoadingException> {
      storage.getOrNull<FakeAsset>(path)
    }
    val reference = storage.getAsync<FakeAsset>(path)
    shouldThrow<AssetLoadingException> {
      runBlocking { reference.await() }
    }
    checkProgress(storage, failed = 1, warn = true)
  }

  @Test
  fun `should handle asynchronous loading exceptions`() {
    // Given:
    val loader = FakeAsyncLoader(
      onAsync = { throw IllegalStateException("Expected.") },
      onSync = {}
    )
    val storage = AssetStorage(useDefaultLoaders = false)
    storage.setLoader { loader }
    val path = "fake path"

    // When:
    shouldThrow<AssetLoadingException> {
      runBlocking { storage.load<FakeAsset>(path) }
    }

    // Then: asset should still be in storage, but rethrowing original exception:
    assertTrue(storage.contains<FakeAsset>(path))
    assertEquals(1, storage.getReferenceCount<FakeAsset>(path))
    shouldThrow<AssetLoadingException> {
      storage.get<FakeAsset>(path)
    }
    shouldThrow<AssetLoadingException> {
      storage.getOrNull<FakeAsset>(path)
    }
    val reference = storage.getAsync<FakeAsset>(path)
    shouldThrow<AssetLoadingException> {
      runBlocking { reference.await() }
    }
    checkProgress(storage, failed = 1, warn = true)
  }

  @Test
  fun `should handle synchronous loading exceptions`() {
    // Given:
    val loader = FakeAsyncLoader(
      onAsync = { },
      onSync = { throw IllegalStateException("Expected.") }
    )
    val storage = AssetStorage(useDefaultLoaders = false)
    storage.setLoader { loader }
    val path = "fake path"

    // When:
    shouldThrow<AssetLoadingException> {
      runBlocking { storage.load<FakeAsset>(path) }
    }

    // Then: asset should still be in storage, but rethrowing original exception:
    assertTrue(storage.contains<FakeAsset>(path))
    assertEquals(1, storage.getReferenceCount<FakeAsset>(path))
    shouldThrow<AssetLoadingException> {
      storage.get<FakeAsset>(path)
    }
    shouldThrow<AssetLoadingException> {
      storage.getOrNull<FakeAsset>(path)
    }
    val reference = storage.getAsync<FakeAsset>(path)
    shouldThrow<AssetLoadingException> {
      runBlocking { reference.await() }
    }
    checkProgress(storage, failed = 1, warn = true)
  }

  @Test
  fun `should throw exception when asset is unloaded asynchronously`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val loadingStarted = CompletableFuture<Boolean>()
    val unloadingFinished = CompletableFuture<Boolean>()
    val exceptionCaught = CompletableFuture<Boolean>()
    val path = "fake path"
    val unloader = newSingleThreadAsyncContext()
    lateinit var exception: Throwable
    storage.setLoader {
      FakeSyncLoader(
        onLoad = {
          loadingStarted.complete(true)
          unloadingFinished.join()
        }
      )
    }
    KtxAsync.launch {
      try {
        storage.load<FakeAsset>(path)
      } catch (expected: Throwable) {
        exception = expected
      }
      exceptionCaught.complete(true)
    }

    // When:
    KtxAsync.launch(unloader) {
      loadingStarted.join()
      storage.unload<FakeAsset>(path)
      unloadingFinished.complete(true)
    }

    // Then:
    exceptionCaught.join()
    assertTrue(exception is UnloadedAssetException)
    assertFalse(storage.contains<FakeAsset>(path))
    shouldThrow<MissingAssetException> {
      storage.get<FakeAsset>(path)
    }
    checkProgress(storage, total = 0, warn = true)
  }

  @Test
  fun `should fail to load assets with loaders that use unsupported AssetManagerWrapper methods`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val path = "fake path"
    storage.setLoader {
      FakeAsyncLoader(
        onAsync = { assetManager -> assetManager.get("Trying to access asset without its type.") },
        onSync = {}
      )
    }

    // When:
    shouldThrow<UnsupportedMethodException> {
      runBlocking {
        storage.load<FakeAsset>(path)
      }
    }

    // Then:
    assertTrue(storage.contains<FakeAsset>(path))
    assertEquals(1, storage.getReferenceCount<FakeAsset>(path))
    shouldThrow<UnsupportedMethodException> {
      storage.get<FakeAsset>(path)
    }
  }

  @Test
  fun `should fail to load assets when loaders try to access unregistered unloaded dependency`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val path = "fake path"
    storage.setLoader {
      FakeAsyncLoader(
        onAsync = { assetManager -> assetManager.get("Missing", FakeAsset::class.java) },
        onSync = {}
      )
    }

    // When:
    shouldThrow<MissingDependencyException> {
      runBlocking {
        storage.load<FakeAsset>(path)
      }
    }

    // Then:
    assertTrue(storage.contains<FakeAsset>(path))
    assertEquals(1, storage.getReferenceCount<FakeAsset>(path))
    shouldThrow<MissingDependencyException> {
      storage.get<FakeAsset>(path)
    }
  }

  @Test
  fun `should not fail to unload asset that was loaded exceptionally`() {
    // Given:
    val loader = FakeSyncLoader(
      onLoad = { throw IllegalStateException("Expected.") }
    )
    val storage = AssetStorage(useDefaultLoaders = false)
    val path = "fake path"
    storage.setLoader { loader }
    storage.logger.level = Logger.NONE // Disposing exception will be logged.
    runBlocking {
      try {
        storage.load<FakeAsset>(path)
      } catch (exception: AssetLoadingException) {
        // Expected.
      }
    }

    // When:
    val unloaded = runBlocking {
      storage.unload<FakeAsset>(path)
    }

    // Then:
    assertTrue(unloaded)
    assertFalse(storage.contains<FakeAsset>(path))
    assertEquals(0, storage.getReferenceCount<FakeAsset>(path))
  }

  @Test
  fun `should fail to load an asset or any of its dependencies with a dependency loader is missing`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val path = "fake path"
    val dependency = "dependency with missing loader"
    storage.setLoader {
      FakeSyncLoader(
        onLoad = {},
        dependencies = GdxArray.with(storage.getAssetDescriptor<Vector2>(dependency))
      )
    }

    // When:
    shouldThrow<MissingLoaderException> {
      runBlocking { storage.load<FakeAsset>("path") }
    }

    // Then:
    assertFalse(storage.contains<FakeAsset>(path))
    assertFalse(storage.contains<Vector2>(dependency))
    checkProgress(storage, total = 0)
  }

  @Test
  fun `should not fail to unload asset that was loaded exceptionally with dependencies`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val path = "path.sync"
    val dependency = "path.async"
    val loader = FakeSyncLoader(
      onLoad = {},
      dependencies = GdxArray.with(storage.getAssetDescriptor<FakeAsset>(dependency))
    )
    val dependencyLoader = FakeAsyncLoader(
      onAsync = {},
      onSync = { throw IllegalStateException("Expected.") }
    )
    storage.setLoader(suffix = ".sync") { loader }
    storage.setLoader(suffix = ".async") { dependencyLoader }
    storage.logger.level = Logger.NONE // Disposing exception will be logged.
    runBlocking {
      try {
        storage.load<FakeAsset>(path)
      } catch (exception: AssetLoadingException) {
        // Expected. Asset fails to load due to dependency.
      }
    }
    assertTrue(storage.contains<FakeAsset>(path))
    assertTrue(storage.contains<FakeAsset>(dependency))

    // When:
    val unloaded = runBlocking {
      storage.unload<FakeAsset>(path)
    }

    // Then:
    assertTrue(unloaded)
    assertFalse(storage.contains<FakeAsset>(path))
    assertEquals(0, storage.getReferenceCount<FakeAsset>(path))
    assertFalse(storage.contains<FakeAsset>(dependency))
    assertEquals(0, storage.getReferenceCount<FakeAsset>(dependency))
  }

  @Test
  fun `should report as unloaded until the asset has finished loading`() {
    // Given:
    val loadingStarted = CompletableFuture<Boolean>()
    val loading = CompletableFuture<Boolean>()
    val loadingFinished = CompletableFuture<Boolean>()
    val loader = FakeSyncLoader(
      onLoad = {
        loadingStarted.join()
        loading.complete(true)
        loadingFinished.join()
      }
    )
    val storage = AssetStorage(useDefaultLoaders = false)
    val path = "fake.path"
    val identifier = storage.getIdentifier<FakeAsset>(path)
    storage.setLoader { loader }

    // When:
    KtxAsync.launch { storage.load(identifier) }

    // Then:
    assertFalse(storage.isLoaded(identifier))

    loadingStarted.complete(true)
    loading.join()
    assertTrue(identifier in storage)
    assertFalse(storage.isLoaded(identifier))
    checkProgress(storage, loaded = 0, total = 1)

    loadingFinished.complete(true)
    runBlocking { storage.getAsync<FakeAsset>(path).await() }
    assertTrue(identifier in storage)
    assertTrue(storage.isLoaded(identifier))
    checkProgress(storage, loaded = 1, total = 1, warn = true)
  }

  @Test
  fun `should utilize asynchronous threads for asset loading`() {
    // Given:
    val asyncThreadNameSuffix = "AssetStorageAsyncThread"
    val renderingThread = getMainRenderingThread()
    val loaders = newAsyncContext(4, threadName = asyncThreadNameSuffix)
    val schedulers = newAsyncContext(4)
    val asyncLoadingThreads: MutableSet<Thread> = Sets.newConcurrentHashSet()
    val syncLoadingThreads: MutableSet<Thread> = Sets.newConcurrentHashSet()
    val storage = AssetStorage(asyncContext = loaders, useDefaultLoaders = false)
    storage.setLoader {
      FakeAsyncLoader(
        onAsync = { asyncLoadingThreads.add(Thread.currentThread()) },
        onSync = { syncLoadingThreads.add(Thread.currentThread()) }
      )
    }

    // When: asynchronously loading multiple assets:
    val tasks = (1..100).map { index ->
      val finished = CompletableDeferred<Boolean>()
      KtxAsync.launch(schedulers) {
        storage.load<FakeAsset>(path = index.toString())
        finished.complete(true)
      }
      finished
    }

    // Then:
    runBlocking { tasks.joinAll() }
    assertEquals(1, syncLoadingThreads.size)
    assertTrue(renderingThread in syncLoadingThreads)
    assertTrue(asyncLoadingThreads.size in 2..4)
    assertTrue(asyncLoadingThreads.all { asyncThreadNameSuffix in it.name })
  }

  @Test
  fun `should dispose of assets that were unloaded before assignment`() {
    // Given:
    val path = "fake path"
    val loadingStarted = CompletableFuture<Boolean>()
    val unloadingFinished = CompletableFuture<Boolean>()
    val loadingFinished = CompletableFuture<Boolean>()
    lateinit var asset: FakeAsset
    lateinit var exception: Throwable
    val storage = AssetStorage(useDefaultLoaders = false)
    storage.setLoader {
      FakeSyncLoader(
        onLoad = {
          asset = it
          loadingStarted.complete(true)
          unloadingFinished.join()
        }
      )
    }

    // When:
    KtxAsync.launch {
      try {
        // Scheduling asset for loading - will be unloaded before it is finished:
        storage.load<FakeAsset>(path)
      } catch (expected: AssetStorageException) {
        // UnloadedAssetException should be caught:
        exception = expected
        loadingFinished.complete(true)
      }
    }
    // Waiting for the loading to start:
    loadingStarted.join()
    runBlocking {
      // Unloading the asset:
      storage.unload<FakeAsset>(path)
      // Loader is waiting for the unloading to finish - unlocking thread:
      unloadingFinished.complete(true)
    }

    // Then:
    asset.disposingFinished.join()
    assertTrue(asset.isDisposed)
    loadingFinished.join()
    shouldThrow<UnloadedAssetException> {
      throw exception
    }
  }

  @Test
  fun `should safely dispose of assets that failed to load`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    storage.logger.level = Logger.NONE
    val path = "fake path"
    storage.setLoader {
      FakeSyncLoader(
        onLoad = { throw IllegalStateException("Expected.") }
      )
    }
    runBlocking {
      try {
        storage.load<FakeAsset>(path)
      } catch (exception: AssetLoadingException) {
        // Expected.
      }
    }

    // When:
    storage.dispose()

    // Then:
    assertFalse(storage.contains<FakeAsset>(path))
  }

  @Test
  fun `should safely dispose of assets that failed to load with error handling`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    storage.logger.level = Logger.NONE
    val path = "fake path"
    lateinit var exception: Throwable
    lateinit var identifier: Identifier<*>
    storage.setLoader {
      FakeSyncLoader(
        onLoad = { throw IllegalStateException("Expected.") }
      )
    }
    runBlocking {
      try {
        storage.load<FakeAsset>(path)
      } catch (exception: AssetLoadingException) {
        // Expected.
      }
    }

    // When:
    runBlocking {
      storage.dispose { id, cause ->
        exception = cause
        identifier = id
      }
    }

    // Then:
    assertFalse(storage.contains<FakeAsset>(path))
    assertTrue(exception is AssetLoadingException)
    assertEquals(storage.getIdentifier<FakeAsset>(path), identifier)
  }

  @Test
  fun `should immediately throw exception when attempting to synchronously load already scheduled asset`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val path = "fake path"
    val loadingStarted = CompletableFuture<Boolean>()
    val loadingFinished = CompletableFuture<Boolean>()
    storage.setLoader {
      FakeSyncLoader(
        onLoad = {
          loadingStarted.complete(true)
          loadingFinished.join()
        }
      )
    }
    val reference = storage.loadAsync<FakeAsset>(path)
    loadingStarted.join()

    // When: asset is not loaded yet:
    shouldThrow<MissingAssetException> {
      storage.loadSync<FakeAsset>(path)
    }

    // Then:
    assertFalse(storage.isLoaded<FakeAsset>(path))
    loadingFinished.complete(true)
    runBlocking { reference.await() }
    assertTrue(storage.isLoaded<FakeAsset>(path))
    // Should still count the reference, since load was called:
    assertEquals(2, storage.getReferenceCount<FakeAsset>(path))
  }

  @Test
  fun `should throw exception when a dependency of a synchronously loaded asset is being loaded asynchronously`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)
    val path = "fake.path"
    val dependency = "path.dep"
    val loadingStarted = CompletableFuture<Boolean>()
    val loadingFinished = CompletableFuture<Boolean>()
    storage.setLoader {
      object: FakeSyncLoader(
        dependencies = GdxArray.with(storage.getAssetDescriptor<FakeAsset>(dependency)),
        onLoad = {}
      ) {
        override fun load(
          assetManager: AssetManager,
          fileName: String?,
          file: FileHandle?,
          parameter: FakeParameters?
        ): FakeAsset {
          assetManager.get(dependency, FakeAsset::class.java)
          return super.load(assetManager, fileName, file, parameter)
        }
      }
    }
    storage.setLoader(suffix = ".dep") {
      FakeSyncLoader(
        onLoad = {
          loadingStarted.complete(true)
          loadingFinished.join()
        }
      )
    }
    val reference = storage.loadAsync<FakeAsset>(dependency)
    loadingStarted.join()

    // When: dependency is not loaded yet:
    shouldThrow<MissingDependencyException> {
      storage.loadSync<FakeAsset>(path)
    }

    // Then:
    assertTrue(storage.isLoaded<FakeAsset>(path))
    assertEquals(1, storage.getReferenceCount<FakeAsset>(path))
    assertFalse(storage.isLoaded<FakeAsset>(dependency))
    loadingFinished.complete(true)
    runBlocking { reference.await() }
    assertTrue(storage.isLoaded<FakeAsset>(dependency))
    // Should still count the reference, since load was called:
    assertEquals(2, storage.getReferenceCount<FakeAsset>(dependency))
  }
}
