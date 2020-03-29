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
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio
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
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.Logger
import com.google.common.collect.Sets
import com.nhaarman.mockitokotlin2.*
import io.kotlintest.matchers.shouldThrow
import kotlinx.coroutines.*
import ktx.assets.TextAssetLoader.TextAssetLoaderParameters
import ktx.async.*
import org.junit.*
import org.junit.Assert.*
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
 * Implementation note: the tests use [runBlocking] to launch the coroutines for simplicity
 * of asserts. Normally [KtxAsync].launch is highly encouraged to build truly asynchronous applications.
 * Using [runBlocking] on the main rendering thread might lead to deadlocks, as the rendering thread
 * is necessary to load some assets (e.g. textures).
 *
 * In a similar manner, some [AssetDescriptor] instances are created manually. In an actual application,
 * using [AssetStorage.getAssetDescriptor] is a much easier way of obtaining [AssetDescriptor] instances.
 */
class AssetStorageTest : AsyncTest() {
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

  @Before
  override fun `setup LibGDX application`() {
    super.`setup LibGDX application`()
    Gdx.audio = OpenALAudio()
  }

  @After
  override fun `exit LibGDX application`() {
    super.`exit LibGDX application`()
    (Gdx.audio as OpenALAudio).dispose()
  }

  /**
   * Testing utility. Obtains instance of [T] by blocking the thread until the
   * [Deferred] is completed. Rethrows any exceptions caught by [Deferred].
   */
  private fun <T> Deferred<T>.joinAndGet(): T = runBlocking { await() }

  @Test
  fun `should load text assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"

    val asset = runBlocking { storage.load<String>(path) }

    assertEquals("Content.", asset)
    assertTrue(storage.isLoaded<String>(path))
    assertSame(asset, storage.get<String>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<String>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<String>(path))
  }

  @Test
  fun `should load text assets with parameters`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"

    // When:
    val asset = runBlocking {
      storage.load(path, parameters = TextAssetLoaderParameters("UTF-8"))
    }

    // Then:
    assertEquals("Content.", asset)
    assertTrue(storage.isLoaded<String>(path))
    assertSame(asset, storage.get<String>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<String>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<String>(path))
  }

  @Test
  fun `should load text assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    val descriptor = AssetDescriptor(path, String::class.java, TextAssetLoaderParameters("UTF-8"))

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertEquals("Content.", asset)
    assertTrue(storage.isLoaded<String>(path))
    assertSame(asset, storage.get<String>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<String>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<String>(path))
  }

  @Test
  fun `should load text assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    val identifier = storage.getIdentifier<String>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertEquals("Content.", asset)
    assertTrue(storage.isLoaded<String>(path))
    assertSame(asset, storage.get<String>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<String>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<String>(path))
  }

  @Test
  fun `should load text assets with identifier and parameters`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    val identifier = storage.getIdentifier<String>(path)

    // When:
    val asset = runBlocking {
      storage.load(identifier, TextAssetLoaderParameters("UTF-8"))
    }

    // Then:
    assertEquals("Content.", asset)
    assertTrue(storage.isLoaded<String>(path))
    assertSame(asset, storage.get<String>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<String>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<String>(path))
  }

  @Test
  fun `should unload text assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    runBlocking { storage.load<String>(path) }

    // When:
    runBlocking { storage.unload<String>(path) }

    // Then:
    assertFalse(storage.isLoaded<String>(path))
    assertEquals(0, storage.getReferenceCount<String>(path))
  }

  @Test
  fun `should load BitmapFont assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "com/badlogic/gdx/utils/arial-15.fnt"
    val dependency = "com/badlogic/gdx/utils/arial-15.png"

    // When:
    val asset = runBlocking { storage.load<BitmapFont>(path) }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(path))
    assertSame(asset, storage.get<BitmapFont>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<BitmapFont>(path))
    assertEquals(listOf(storage.getIdentifier<Texture>(dependency)), storage.getDependencies<BitmapFont>(path))
    // Font dependencies:
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertEquals(1, storage.getReferenceCount<Texture>(dependency))
    assertSame(asset.region.texture, storage.get<Texture>(dependency).joinAndGet())

    storage.dispose()
  }

  @Test
  fun `should load BitmapFont assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "com/badlogic/gdx/utils/arial-15.fnt"
    val dependency = "com/badlogic/gdx/utils/arial-15.png"
    val descriptor = AssetDescriptor(path, BitmapFont::class.java)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(path))
    assertSame(asset, storage.get<BitmapFont>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<BitmapFont>(path))
    assertEquals(listOf(storage.getIdentifier<Texture>(dependency)), storage.getDependencies<BitmapFont>(path))
    // Font dependencies:
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertEquals(1, storage.getReferenceCount<Texture>(dependency))
    assertSame(asset.region.texture, storage.get<Texture>(dependency).joinAndGet())

    storage.dispose()
  }

  @Test
  fun `should load BitmapFont assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "com/badlogic/gdx/utils/arial-15.fnt"
    val dependency = "com/badlogic/gdx/utils/arial-15.png"
    val identifier = storage.getIdentifier<BitmapFont>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(path))
    assertSame(asset, storage.get<BitmapFont>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<BitmapFont>(path))
    assertEquals(listOf(storage.getIdentifier<Texture>(dependency)), storage.getDependencies<BitmapFont>(path))
    // Font dependencies:
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertEquals(1, storage.getReferenceCount<Texture>(dependency))
    assertSame(asset.region.texture, storage.get<Texture>(dependency).joinAndGet())

    storage.dispose()
  }

  @Test
  fun `should unload BitmapFont with dependencies`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "com/badlogic/gdx/utils/arial-15.fnt"
    val dependency = "com/badlogic/gdx/utils/arial-15.png"
    runBlocking { storage.load<BitmapFont>(path) }

    // When:
    runBlocking { storage.unload<BitmapFont>(path) }

    // Then:
    assertFalse(storage.isLoaded<BitmapFont>(path))
    assertEquals(0, storage.getReferenceCount<BitmapFont>(path))
    assertFalse(storage.isLoaded<Texture>(dependency))
    assertEquals(0, storage.getReferenceCount<Texture>(path))
  }

  @Test
  fun `should load Music assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/sound.ogg"

    // When:
    val asset = runBlocking { storage.load<Music>(path) }

    // Then:
    assertTrue(storage.isLoaded<Music>(path))
    assertSame(asset, storage.get<Music>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Music>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Music>(path))

    storage.dispose()
  }

  @Test
  fun `should load Music assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/sound.ogg"
    val descriptor = AssetDescriptor(path, Music::class.java)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<Music>(path))
    assertSame(asset, storage.get<Music>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Music>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Music>(path))

    storage.dispose()
  }


  @Test
  fun `should load Music assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/sound.ogg"
    val identifier = storage.getIdentifier<Music>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<Music>(path))
    assertSame(asset, storage.get<Music>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Music>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Music>(path))

    storage.dispose()
  }

  @Test
  fun `should unload Music assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/sound.ogg"
    runBlocking { storage.load<Music>(path) }

    // When:
    runBlocking { storage.unload<Music>(path) }

    // Then:
    assertFalse(storage.isLoaded<Music>(path))
    assertEquals(0, storage.getReferenceCount<Music>(path))
  }


  @Test
  fun `should load Sound assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/sound.ogg"

    // When:
    val asset = runBlocking { storage.load<Sound>(path) }

    // Then:
    assertTrue(storage.isLoaded<Sound>(path))
    assertSame(asset, storage.get<Sound>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Sound>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Sound>(path))

    storage.dispose()
  }

  @Test
  fun `should load Sound assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/sound.ogg"
    val descriptor = AssetDescriptor(path, Sound::class.java)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<Sound>(path))
    assertSame(asset, storage.get<Sound>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Sound>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Sound>(path))

    storage.dispose()
  }

  @Test
  fun `should load Sound assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/sound.ogg"
    val identifier = storage.getIdentifier<Sound>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<Sound>(path))
    assertSame(asset, storage.get<Sound>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Sound>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Sound>(path))

    storage.dispose()
  }

  @Test
  fun `should unload Sound assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/sound.ogg"
    runBlocking { storage.load<Sound>(path) }

    // When:
    runBlocking { storage.unload<Sound>(path) }

    // Then:
    assertFalse(storage.isLoaded<Sound>(path))
    assertEquals(0, storage.getReferenceCount<Sound>(path))
  }

  @Test
  fun `should load TextureAtlas assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.atlas"
    val dependency = "ktx/assets/async/texture.png"

    // When:
    val asset = runBlocking { storage.load<TextureAtlas>(path) }

    // Then:
    assertTrue(storage.isLoaded<TextureAtlas>(path))
    assertSame(asset, storage.get<TextureAtlas>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<TextureAtlas>(path))
    assertEquals(listOf(storage.getIdentifier<Texture>(dependency)), storage.getDependencies<TextureAtlas>(path))
    // Atlas dependencies:
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertSame(asset.textures.first(), storage.get<Texture>(dependency).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Texture>(dependency))

    storage.dispose()
  }

  @Test
  fun `should load TextureAtlas assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.atlas"
    val dependency = "ktx/assets/async/texture.png"
    val descriptor = AssetDescriptor(path, TextureAtlas::class.java)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<TextureAtlas>(path))
    assertSame(asset, storage.get<TextureAtlas>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<TextureAtlas>(path))
    assertEquals(listOf(storage.getIdentifier<Texture>(dependency)), storage.getDependencies<TextureAtlas>(path))
    // Atlas dependencies:
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertSame(asset.textures.first(), storage.get<Texture>(dependency).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Texture>(dependency))

    storage.dispose()
  }

  @Test
  fun `should load TextureAtlas assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.atlas"
    val dependency = "ktx/assets/async/texture.png"
    val identifier = storage.getIdentifier<TextureAtlas>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<TextureAtlas>(path))
    assertSame(asset, storage.get<TextureAtlas>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<TextureAtlas>(path))
    assertEquals(listOf(storage.getIdentifier<Texture>(dependency)), storage.getDependencies<TextureAtlas>(path))
    // Atlas dependencies:
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertSame(asset.textures.first(), storage.get<Texture>(dependency).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Texture>(dependency))

    storage.dispose()
  }

  @Test
  fun `should unload TextureAtlas assets with dependencies`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.atlas"
    val dependency = "ktx/assets/async/texture.png"
    runBlocking { storage.load<TextureAtlas>(path) }

    // When:
    runBlocking { storage.unload<TextureAtlas>(path) }

    // Then:
    assertFalse(storage.isLoaded<TextureAtlas>(path))
    assertEquals(0, storage.getReferenceCount<TextureAtlas>(path))
    assertFalse(storage.isLoaded<Texture>(dependency))
    assertEquals(0, storage.getReferenceCount<Texture>(dependency))
  }

  @Test
  fun `should load Texture assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"

    // When:
    val asset = runBlocking { storage.load<Texture>(path) }

    // Then:
    assertTrue(storage.isLoaded<Texture>(path))
    assertSame(asset, storage.get<Texture>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Texture>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Texture>(path))

    storage.dispose()
  }

  @Test
  fun `should load Texture assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"
    val descriptor = AssetDescriptor(path, Texture::class.java)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<Texture>(path))
    assertSame(asset, storage.get<Texture>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Texture>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Texture>(path))

    storage.dispose()
  }

  @Test
  fun `should load Texture assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"
    val identifier = storage.getIdentifier<Texture>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<Texture>(path))
    assertSame(asset, storage.get<Texture>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Texture>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Texture>(path))

    storage.dispose()
  }

  @Test
  fun `should unload Texture assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"
    runBlocking { storage.load<Texture>(path) }

    // When:
    runBlocking { storage.unload<Texture>(path) }

    // Then:
    assertFalse(storage.isLoaded<Texture>(path))
    assertEquals(0, storage.getReferenceCount<Texture>(path))

    storage.dispose()
  }

  @Test
  fun `should load Pixmap assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"

    // When:
    val asset = runBlocking { storage.load<Pixmap>(path) }

    // Then:
    assertTrue(storage.isLoaded<Pixmap>(path))
    assertSame(asset, storage.get<Pixmap>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Pixmap>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Pixmap>(path))

    storage.dispose()
  }

  @Test
  fun `should load Pixmap assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"
    val descriptor = AssetDescriptor(path, Pixmap::class.java)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<Pixmap>(path))
    assertSame(asset, storage.get<Pixmap>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Pixmap>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Pixmap>(path))

    storage.dispose()
  }

  @Test
  fun `should load Pixmap assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"
    val identifier = storage.getIdentifier<Pixmap>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<Pixmap>(path))
    assertSame(asset, storage.get<Pixmap>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Pixmap>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Pixmap>(path))

    storage.dispose()
  }

  @Test
  fun `should unload Pixmap assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"
    runBlocking { storage.load<Pixmap>(path) }

    // When:
    runBlocking { storage.unload<Pixmap>(path) }

    // Then:
    assertFalse(storage.isLoaded<Pixmap>(path))
    assertEquals(0, storage.getReferenceCount<Pixmap>(path))

    storage.dispose()
  }

  @Test
  fun `should load Skin assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.json"
    val atlas = "ktx/assets/async/skin.atlas"
    val texture = "ktx/assets/async/texture.png"

    // When:
    val asset = runBlocking { storage.load<Skin>(path) }

    // Then:
    assertTrue(storage.isLoaded<Skin>(path))
    assertSame(asset, storage.get<Skin>(path).joinAndGet())
    assertNotNull(asset.get("default", Button.ButtonStyle::class.java))
    assertEquals(1, storage.getReferenceCount<Skin>(path))
    assertEquals(listOf(storage.getIdentifier<TextureAtlas>(atlas)), storage.getDependencies<Skin>(path))
    // Skin dependencies:
    assertTrue(storage.isLoaded<TextureAtlas>(atlas))
    assertEquals(1, storage.getReferenceCount<TextureAtlas>(atlas))
    assertSame(asset.atlas, storage.get<TextureAtlas>(atlas).joinAndGet())
    assertEquals(listOf(storage.getIdentifier<Texture>(texture)), storage.getDependencies<TextureAtlas>(atlas))
    // Atlas dependencies:
    assertTrue(storage.isLoaded<Texture>(texture))
    assertSame(asset.atlas.textures.first(), storage.get<Texture>(texture).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Texture>(texture))

    storage.dispose()
  }

  @Test
  fun `should load Skin assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.json"
    val atlas = "ktx/assets/async/skin.atlas"
    val texture = "ktx/assets/async/texture.png"
    val descriptor = AssetDescriptor(path, Skin::class.java)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<Skin>(path))
    assertSame(asset, storage.get<Skin>(path).joinAndGet())
    assertNotNull(asset.get("default", Button.ButtonStyle::class.java))
    assertEquals(1, storage.getReferenceCount<Skin>(path))
    assertEquals(listOf(storage.getIdentifier<TextureAtlas>(atlas)), storage.getDependencies<Skin>(path))
    // Skin dependencies:
    assertTrue(storage.isLoaded<TextureAtlas>(atlas))
    assertEquals(1, storage.getReferenceCount<TextureAtlas>(atlas))
    assertSame(asset.atlas, storage.get<TextureAtlas>(atlas).joinAndGet())
    assertEquals(listOf(storage.getIdentifier<Texture>(texture)), storage.getDependencies<TextureAtlas>(atlas))
    // Atlas dependencies:
    assertTrue(storage.isLoaded<Texture>(texture))
    assertSame(asset.atlas.textures.first(), storage.get<Texture>(texture).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Texture>(texture))

    storage.dispose()
  }

  @Test
  fun `should load Skin assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.json"
    val atlas = "ktx/assets/async/skin.atlas"
    val texture = "ktx/assets/async/texture.png"
    val identifier = storage.getIdentifier<Skin>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<Skin>(path))
    assertSame(asset, storage.get<Skin>(path).joinAndGet())
    assertNotNull(asset.get("default", Button.ButtonStyle::class.java))
    assertEquals(1, storage.getReferenceCount<Skin>(path))
    assertEquals(listOf(storage.getIdentifier<TextureAtlas>(atlas)), storage.getDependencies<Skin>(path))
    // Skin dependencies:
    assertTrue(storage.isLoaded<TextureAtlas>(atlas))
    assertEquals(1, storage.getReferenceCount<TextureAtlas>(atlas))
    assertSame(asset.atlas, storage.get<TextureAtlas>(atlas).joinAndGet())
    assertEquals(listOf(storage.getIdentifier<Texture>(texture)), storage.getDependencies<TextureAtlas>(atlas))
    // Atlas dependencies:
    assertTrue(storage.isLoaded<Texture>(texture))
    assertSame(asset.atlas.textures.first(), storage.get<Texture>(texture).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Texture>(texture))

    storage.dispose()
  }

  @Test
  fun `should unload Skin assets with dependencies`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.json"
    val atlas = "ktx/assets/async/skin.atlas"
    val texture = "ktx/assets/async/texture.png"
    runBlocking { storage.load<Skin>(path) }

    // When:
    runBlocking { storage.unload<Skin>(path) }

    // Then:
    assertFalse(storage.isLoaded<Skin>(path))
    assertEquals(0, storage.getReferenceCount<Skin>(path))
    assertFalse(storage.isLoaded<TextureAtlas>(atlas))
    assertEquals(0, storage.getReferenceCount<TextureAtlas>(atlas))
    assertFalse(storage.isLoaded<Texture>(texture))
    assertEquals(0, storage.getReferenceCount<Texture>(texture))
  }

  @Test
  fun `should load I18NBundle assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/i18n"

    // When:
    val asset = runBlocking { storage.load<I18NBundle>(path) }

    // Then:
    assertTrue(storage.isLoaded<I18NBundle>(path))
    assertEquals("Value.", asset["key"])
    assertSame(asset, storage.get<I18NBundle>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<I18NBundle>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<I18NBundle>(path))

    storage.dispose()
  }

  @Test
  fun `should load I18NBundle assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/i18n"
    val descriptor = AssetDescriptor(path, I18NBundle::class.java)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<I18NBundle>(path))
    assertEquals("Value.", asset["key"])
    assertSame(asset, storage.get<I18NBundle>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<I18NBundle>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<I18NBundle>(path))

    storage.dispose()
  }

  @Test
  fun `should load I18NBundle assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/i18n"
    val identifier = storage.getIdentifier<I18NBundle>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<I18NBundle>(path))
    assertEquals("Value.", asset["key"])
    assertSame(asset, storage.get<I18NBundle>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<I18NBundle>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<I18NBundle>(path))

    storage.dispose()
  }

  @Test
  fun `should unload I18NBundle assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/i18n"
    runBlocking { storage.load<I18NBundle>(path) }

    // When:
    runBlocking { storage.unload<I18NBundle>(path) }

    // Then:
    assertFalse(storage.isLoaded<I18NBundle>(path))
    assertEquals(0, storage.getReferenceCount<I18NBundle>(path))
  }

  @Test
  fun `should load ParticleEffect assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/particle.p2d"

    // When:
    val asset = runBlocking { storage.load<ParticleEffect>(path) }

    // Then:
    assertTrue(storage.isLoaded<ParticleEffect>(path))
    assertSame(asset, storage.get<ParticleEffect>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<ParticleEffect>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<ParticleEffect>(path))

    storage.dispose()
  }

  @Test
  fun `should load ParticleEffect assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/particle.p2d"
    val descriptor = AssetDescriptor(path, ParticleEffect::class.java)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<ParticleEffect>(path))
    assertSame(asset, storage.get<ParticleEffect>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<ParticleEffect>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<ParticleEffect>(path))

    storage.dispose()
  }

  @Test
  fun `should load ParticleEffect assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/particle.p2d"
    val identifier = storage.getIdentifier<ParticleEffect>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<ParticleEffect>(path))
    assertSame(asset, storage.get<ParticleEffect>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<ParticleEffect>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<ParticleEffect>(path))

    storage.dispose()
  }

  @Test
  fun `should unload ParticleEffect assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/particle.p2d"
    runBlocking { storage.load<ParticleEffect>(path) }

    // When:
    runBlocking { storage.unload<ParticleEffect>(path) }

    // Then:
    assertFalse(storage.isLoaded<ParticleEffect>(path))
    assertEquals(0, storage.getReferenceCount<ParticleEffect>(path))
  }

  @Test
  fun `should load ParticleEffect3D assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/particle.p3d"
    val dependency = "ktx/assets/async/texture.png"

    // When:
    val asset = runBlocking { storage.load<ParticleEffect3D>(path) }

    // Then:
    assertTrue(storage.isLoaded<ParticleEffect3D>(path))
    assertSame(asset, storage.get<ParticleEffect3D>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<ParticleEffect3D>(path))
    assertEquals(listOf(storage.getIdentifier<Texture>(dependency)), storage.getDependencies<ParticleEffect3D>(path))
    // Particle dependencies:
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertNotNull(storage.get<Texture>(dependency).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Texture>(dependency))

    storage.dispose()
  }

  @Test
  fun `should load ParticleEffect3D assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/particle.p3d"
    val descriptor = AssetDescriptor(path, ParticleEffect3D::class.java)
    val dependency = "ktx/assets/async/texture.png"

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<ParticleEffect3D>(path))
    assertSame(asset, storage.get<ParticleEffect3D>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<ParticleEffect3D>(path))
    assertEquals(listOf(storage.getIdentifier<Texture>(dependency)), storage.getDependencies<ParticleEffect3D>(path))
    // Particle dependencies:
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertNotNull(storage.get<Texture>(dependency).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Texture>(dependency))

    storage.dispose()
  }

  @Test
  fun `should load ParticleEffect3D assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/particle.p3d"
    val dependency = "ktx/assets/async/texture.png"
    val identifier = storage.getIdentifier<ParticleEffect3D>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<ParticleEffect3D>(path))
    assertSame(asset, storage.get<ParticleEffect3D>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<ParticleEffect3D>(path))
    assertEquals(listOf(storage.getIdentifier<Texture>(dependency)), storage.getDependencies<ParticleEffect3D>(path))
    // Particle dependencies:
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertNotNull(storage.get<Texture>(dependency).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Texture>(dependency))

    storage.dispose()
  }

  @Test
  fun `should unload ParticleEffect3D assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/particle.p3d"
    val dependency = "ktx/assets/async/texture.png"
    runBlocking { storage.load<ParticleEffect3D>(path) }

    // When:
    runBlocking { storage.unload<ParticleEffect3D>(path) }

    // Then:
    assertFalse(storage.isLoaded<ParticleEffect3D>(path))
    assertEquals(0, storage.getReferenceCount<ParticleEffect3D>(path))
    assertFalse(storage.isLoaded<Texture>(dependency))
    assertEquals(0, storage.getReferenceCount<Texture>(dependency))
  }

  @Test
  fun `should load OBJ Model assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.obj"

    // When:
    val asset = runBlocking { storage.load<Model>(path) }

    // Then:
    assertTrue(storage.isLoaded<Model>(path))
    assertSame(asset, storage.get<Model>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Model>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Model>(path))

    storage.dispose()
  }

  @Test
  fun `should load OBJ Model assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.obj"
    val descriptor = AssetDescriptor(path, Model::class.java)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<Model>(path))
    assertSame(asset, storage.get<Model>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Model>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Model>(path))

    storage.dispose()
  }

  @Test
  fun `should load OBJ Model assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.obj"
    val identifier = storage.getIdentifier<Model>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<Model>(path))
    assertSame(asset, storage.get<Model>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Model>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Model>(path))

    storage.dispose()
  }

  @Test
  fun `should unload OBJ Model assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.obj"
    runBlocking { storage.load<Model>(path) }

    // When:
    runBlocking { storage.unload<Model>(path) }

    // Then:
    assertFalse(storage.isLoaded<Model>(path))
    assertEquals(0, storage.getReferenceCount<Model>(path))
  }

  @Test
  fun `should load G3DJ Model assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.g3dj"

    // When:
    val asset = runBlocking { storage.load<Model>(path) }

    // Then:
    assertTrue(storage.isLoaded<Model>(path))
    assertSame(asset, storage.get<Model>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Model>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Model>(path))

    storage.dispose()
  }

  @Test
  fun `should load G3DJ Model assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.g3dj"
    val descriptor = AssetDescriptor(path, Model::class.java)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<Model>(path))
    assertSame(asset, storage.get<Model>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Model>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Model>(path))

    storage.dispose()
  }

  @Test
  fun `should load G3DJ Model assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.g3dj"
    val identifier = storage.getIdentifier<Model>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<Model>(path))
    assertSame(asset, storage.get<Model>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Model>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Model>(path))

    storage.dispose()
  }

  @Test
  fun `should unload G3DJ Model assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.g3dj"
    runBlocking { storage.load<Model>(path) }

    // When:
    runBlocking { storage.unload<Model>(path) }

    // Then:
    assertFalse(storage.isLoaded<Model>(path))
    assertEquals(0, storage.getReferenceCount<Model>(path))
  }

  @Test
  fun `should load G3DB Model assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.g3db"

    // When:
    val asset = runBlocking { storage.load<Model>(path) }

    // Then:
    assertTrue(storage.isLoaded<Model>(path))
    assertSame(asset, storage.get<Model>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Model>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Model>(path))

    storage.dispose()
  }

  @Test
  fun `should load G3DB Model assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.g3db"
    val descriptor = AssetDescriptor(path, Model::class.java)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<Model>(path))
    assertSame(asset, storage.get<Model>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Model>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Model>(path))

    storage.dispose()
  }

  @Test
  fun `should load G3DB Model assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.g3db"
    val descriptor = storage.getIdentifier<Model>(path)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<Model>(path))
    assertSame(asset, storage.get<Model>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Model>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Model>(path))

    storage.dispose()
  }

  @Test
  fun `should unload G3DB Model assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.g3db"
    runBlocking { storage.load<Model>(path) }

    // When:
    runBlocking { storage.unload<Model>(path) }

    // Then:
    assertFalse(storage.isLoaded<Model>(path))
    assertEquals(0, storage.getReferenceCount<Model>(path))
  }

  @Test
  fun `should load ShaderProgram assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/shader.frag"
    // Silencing logs - shader will fail to compile, as GL is mocked:
    storage.logger.level = Logger.NONE

    // When:
    val asset = runBlocking { storage.load<ShaderProgram>(path) }

    // Then:
    assertTrue(storage.isLoaded<ShaderProgram>(path))
    assertSame(asset, storage.get<ShaderProgram>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<ShaderProgram>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<ShaderProgram>(path))

    storage.dispose()
  }

  @Test
  fun `should load ShaderProgram assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/shader.vert"
    val descriptor = AssetDescriptor(path, ShaderProgram::class.java)
    // Silencing logs - shader will fail to compile, as GL is mocked:
    storage.logger.level = Logger.NONE

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<ShaderProgram>(path))
    assertSame(asset, storage.get<ShaderProgram>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<ShaderProgram>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<ShaderProgram>(path))

    storage.dispose()
  }

  @Test
  fun `should load ShaderProgram assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/shader.vert"
    val identifier = storage.getIdentifier<ShaderProgram>(path)
    // Silencing logs - shader will fail to compile, as GL is mocked:
    storage.logger.level = Logger.NONE

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<ShaderProgram>(path))
    assertSame(asset, storage.get<ShaderProgram>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<ShaderProgram>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<ShaderProgram>(path))

    storage.dispose()
  }

  @Test
  fun `should unload ShaderProgram assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/shader.frag"
    // Silencing logs - shader will fail to compile, as GL is mocked:
    storage.logger.level = Logger.NONE
    runBlocking { storage.load<ShaderProgram>(path) }

    // When:
    runBlocking { storage.unload<ShaderProgram>(path) }

    // Then:
    assertFalse(storage.isLoaded<ShaderProgram>(path))
    assertEquals(0, storage.getReferenceCount<ShaderProgram>(path))
  }

  @Test
  fun `should load Cubemap assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/cubemap.zktx"

    // When:
    val asset = runBlocking { storage.load<Cubemap>(path) }

    // Then:
    assertTrue(storage.isLoaded<Cubemap>(path))
    assertSame(asset, storage.get<Cubemap>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Cubemap>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Cubemap>(path))

    storage.dispose()
  }

  @Test
  fun `should load Cubemap assets with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/cubemap.zktx"
    val descriptor = AssetDescriptor(path, Cubemap::class.java)

    // When:
    val asset = runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(storage.isLoaded<Cubemap>(path))
    assertSame(asset, storage.get<Cubemap>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Cubemap>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Cubemap>(path))

    storage.dispose()
  }

  @Test
  fun `should load Cubemap assets with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/cubemap.zktx"
    val identifier = storage.getIdentifier<Cubemap>(path)

    // When:
    val asset = runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(storage.isLoaded<Cubemap>(path))
    assertSame(asset, storage.get<Cubemap>(path).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Cubemap>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Cubemap>(path))

    storage.dispose()
  }

  @Test
  fun `should unload Cubemap assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/cubemap.zktx"
    runBlocking { storage.load<Cubemap>(path) }

    // When:
    runBlocking { storage.unload<Cubemap>(path) }

    // Then:
    assertFalse(storage.isLoaded<Cubemap>(path))
    assertEquals(0, storage.getReferenceCount<Cubemap>(path))
  }

  @Test
  fun `should return deferred that throws exception when attempting to get unloaded asset`() {
    // Given:
    val storage = AssetStorage()

    // When:
    val result = storage.get<String>("ktx/assets/async/string.txt")

    // Expect:
    shouldThrow<MissingAssetException> {
      runBlocking { result.await() }
    }
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
    assertEquals("Content.", storage.get<String>(path).joinAndGet())
    assertEquals(emptyList<String>(), storage.getDependencies<String>(path))
  }

  @Test
  fun `should obtain loaded asset with identifier`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val identifier = storage.getIdentifier<String>("ktx/assets/async/string.txt")

    // When:
    runBlocking { storage.load(identifier) }

    // Then:
    assertTrue(identifier in storage)
    assertTrue(storage.isLoaded(identifier))
    assertEquals("Content.", storage[identifier].joinAndGet())
    assertEquals(emptyList<String>(), storage.getDependencies(identifier))
  }

  @Test
  fun `should obtain loaded asset with descriptor`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = storage.getAssetDescriptor<String>("ktx/assets/async/string.txt")

    // When:
    runBlocking { storage.load(descriptor) }

    // Then:
    assertTrue(descriptor in storage)
    assertTrue(storage.isLoaded(descriptor))
    assertEquals("Content.", storage[descriptor].joinAndGet())
    assertEquals(emptyList<String>(), storage.getDependencies(descriptor))
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
    assertSame(asset, storage.get<Vector2>(fakePath).joinAndGet())
    assertEquals(1, storage.getReferenceCount<Vector2>(fakePath))
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
    assertSame(asset, storage[descriptor].joinAndGet())
    assertEquals(1, storage.getReferenceCount(descriptor))
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
    assertSame(asset, storage[identifier].joinAndGet())
    assertEquals(1, storage.getReferenceCount(identifier))
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
    assertSame(asset, storage.get<Texture>(path).joinAndGet())
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
    assertSame(asset, storage.get<Pixmap>(path).joinAndGet())

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
    assertNotSame(storage.get<Texture>(path).joinAndGet(), storage.get<Pixmap>(path).joinAndGet())

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
    assertSame(tasks[0].joinAndGet(), storage.get<Texture>(firstPath).joinAndGet())
    assertSame(tasks[1].joinAndGet(), storage.get<Model>(secondPath).joinAndGet())

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
        val asset = storage.load(descriptor)
        loadedAssets[asset] = true
      }
    }

    // Then:
    assertEquals(3, storage.getReferenceCount(descriptor))
    dependencies.forEach {
      assertEquals(3, storage.getReferenceCount(it))
    }
    assertEquals(1, loadedAssets.size)

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
    runBlocking {
      repeat(3) {
        val asset = storage.load(identifier)
        loadedAssets[asset] = true
      }
    }

    // Then:
    assertEquals(3, storage.getReferenceCount(identifier))
    dependencies.forEach {
      assertEquals(3, storage.getReferenceCount(it))
    }
    assertEquals(1, loadedAssets.size)

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
    assertEquals(1, assets.map { it.joinAndGet() }.toSet().size)

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
    assertEquals("Content.", storage.get<String>(path).joinAndGet())

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
      storage.get<BitmapFont>(path).joinAndGet().region.texture,
      storage.get<Texture>(dependency).joinAndGet()
    )

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
    val skin = storage.get<Skin>(path).joinAndGet()
    val atlas = storage.get<TextureAtlas>(dependency).joinAndGet()
    val texture = storage.get<Texture>(nestedDependency).joinAndGet()
    assertSame(skin.atlas, atlas)
    assertSame(atlas.textures.first(), texture)

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

    // When: spawning 1000 coroutines that randomly load or unload the asset:
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
    // Does not extend Synchronous/AsynchronousAssetLoader:
    val invalidLoader = mock<Loader<Int>>()

    // Expect:
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
  }

  @Test
  fun `should dispose of multiple assets of different types without errors`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    storage.logger.level = Logger.NONE
    val assets = listOf(
      storage.getIdentifier<String>("ktx/assets/async/string.txt"),
      storage.getIdentifier<BitmapFont>("com/badlogic/gdx/utils/arial-15.fnt"),
      storage.getIdentifier<Music>("ktx/assets/async/sound.ogg"),
      storage.getIdentifier<TextureAtlas>("ktx/assets/async/skin.atlas"),
      storage.getIdentifier<Texture>("ktx/assets/async/texture.png"),
      storage.getIdentifier<Skin>("ktx/assets/async/skin.json"),
      storage.getIdentifier<I18NBundle>("ktx/assets/async/i18n"),
      storage.getIdentifier<ParticleEffect>("ktx/assets/async/particle.p2d"),
      storage.getIdentifier<ParticleEffect3D>("ktx/assets/async/particle.p3d"),
      storage.getIdentifier<Model>("ktx/assets/async/model.obj"),
      storage.getIdentifier<Model>("ktx/assets/async/model.g3dj"),
      storage.getIdentifier<Model>("ktx/assets/async/model.g3db"),
      storage.getIdentifier<ShaderProgram>("ktx/assets/async/shader.frag"),
      storage.getIdentifier<Cubemap>("ktx/assets/async/cubemap.zktx")
    )
    runBlocking {
      assets.forEach {
        storage.load(it)
        assertTrue(storage.isLoaded(it))
      }
    }

    // When:
    storage.dispose()

    // Then:
    assets.forEach {
      assertFalse(it in storage)
      assertFalse(storage.isLoaded(it))
      assertEquals(0, storage.getReferenceCount(it))
      assertEquals(emptyList<String>(), storage.getDependencies(it))
      shouldThrow<MissingAssetException> {
        storage[it].joinAndGet()
      }
    }
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
  class FakeSyncLoader(
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
  fun `should execute synchronous loading on rendering thread`() {
    // Given:
    val isRenderingThread = CompletableFuture<Boolean>()
    val loader = FakeSyncLoader(
      onLoad = { isRenderingThread.complete(KtxAsync.isOnRenderingThread()) }
    )
    val storage = AssetStorage(useDefaultLoaders = false)
    storage.setLoader { loader }

    // When:
    runBlocking { storage.load<FakeAsset>("fake.path") }

    // Then:
    assertTrue(isRenderingThread.getNow(false))
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
      storage.get<FakeAsset>(path).joinAndGet()
    }
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
      storage.get<FakeAsset>(path).joinAndGet()
    }
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
      storage.get<FakeAsset>(path).joinAndGet()
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
      storage.get<FakeAsset>(path).joinAndGet()
    }
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
      storage.get<FakeAsset>(path).joinAndGet()
    }
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
      storage.get<FakeAsset>(path).joinAndGet()
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
    KtxAsync.launch { storage.load<FakeAsset>(path) }

    // Then:
    assertFalse(storage.isLoaded<FakeAsset>(path))

    loadingStarted.complete(true)
    loading.join()
    assertTrue(identifier in storage)
    assertFalse(storage.isLoaded(identifier))

    loadingFinished.complete(true)
    runBlocking { storage.get<FakeAsset>(path).await() }
    assertTrue(identifier in storage)
    assertTrue(storage.isLoaded(identifier))
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
    assertEquals(4, asyncLoadingThreads.size)
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
}
