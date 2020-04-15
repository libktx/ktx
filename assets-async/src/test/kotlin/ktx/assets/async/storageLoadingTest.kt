package ktx.assets.async

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio
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
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.Logger
import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.matchers.shouldThrow
import kotlinx.coroutines.runBlocking
import ktx.assets.TextAssetLoader
import ktx.async.AsyncTest
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.TestName
import java.util.*
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect as ParticleEffect3D

/**
 * [AssetStorage] has 3 main variants of asset loading: [AssetStorage.load], [AssetStorage.loadAsync]
 * and [AssetStorage.loadSync]. To test each and every one, a common abstract test suite is provided.
 *
 * This test suite ensures that each method supports loading of every default asset type
 * and performs basic asset loading logic tests.
 *
 * Note that variants consuming [String] path and reified asset types could not be easily tested,
 * as they cannot be invoked in abstract methods. However, since all of them are just aliases and
 * contain no logic other than [AssetDescriptor] or [Identifier] initiation, the associated loading
 * methods are still tested.
 *
 * See also: [AssetStorageTest].
 */
abstract class AbstractAssetStorageLoadingTest : AsyncTest() {
  @get:Rule
  var testName = TestName()

  /**
   * Must be overridden with the tested loading method variant.
   * Blocks the current thread until the selected asset is loaded.
   */
  protected abstract fun <T> AssetStorage.testLoad(
    path: String,
    type: Class<T>,
    parameters: AssetLoaderParameters<T>?
  ): T

  private inline fun <reified T> AssetStorage.testLoad(
    path: String,
    parameters: AssetLoaderParameters<T>? = null
  ): T = testLoad(path, T::class.java, parameters)

  // --- Asset support tests:

  @Test
  fun `should load text assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"

    // When:
    val asset = storage.testLoad<String>(path)

    // Then:
    assertEquals("Content.", asset)
    assertTrue(storage.isLoaded<String>(path))
    assertSame(asset, storage.get<String>(path))
    assertEquals(1, storage.getReferenceCount<String>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<String>(path))
  }

  @Test
  fun `should load text assets with parameters`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"

    // When:
    val asset = storage.testLoad(path, parameters = TextAssetLoader.TextAssetLoaderParameters("UTF-8"))

    // Then:
    assertEquals("Content.", asset)
    assertTrue(storage.isLoaded<String>(path))
    assertSame(asset, storage.get<String>(path))
    assertEquals(1, storage.getReferenceCount<String>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<String>(path))
  }

  @Test
  fun `should unload text assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    storage.testLoad<String>(path)

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
    val asset = storage.testLoad<BitmapFont>(path)

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(path))
    assertSame(asset, storage.get<BitmapFont>(path))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(path))
    assertEquals(listOf(storage.getIdentifier<Texture>(dependency)), storage.getDependencies<BitmapFont>(path))
    // Font dependencies:
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertEquals(1, storage.getReferenceCount<Texture>(dependency))
    assertSame(asset.region.texture, storage.get<Texture>(dependency))

    storage.dispose()
  }

  @Test
  fun `should unload BitmapFont with dependencies`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "com/badlogic/gdx/utils/arial-15.fnt"
    val dependency = "com/badlogic/gdx/utils/arial-15.png"
    storage.testLoad<BitmapFont>(path)

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
    val asset = storage.testLoad<Music>(path)

    // Then:
    assertTrue(storage.isLoaded<Music>(path))
    assertSame(asset, storage.get<Music>(path))
    assertEquals(1, storage.getReferenceCount<Music>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Music>(path))

    storage.dispose()
  }

  @Test
  fun `should unload Music assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/sound.ogg"
    storage.testLoad<Music>(path)

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
    val asset = storage.testLoad<Sound>(path)

    // Then:
    assertTrue(storage.isLoaded<Sound>(path))
    assertSame(asset, storage.get<Sound>(path))
    assertEquals(1, storage.getReferenceCount<Sound>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Sound>(path))

    storage.dispose()
  }

  @Test
  fun `should unload Sound assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/sound.ogg"
    storage.testLoad<Sound>(path)

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
    val asset = storage.testLoad<TextureAtlas>(path)

    // Then:
    assertTrue(storage.isLoaded<TextureAtlas>(path))
    assertSame(asset, storage.get<TextureAtlas>(path))
    assertEquals(1, storage.getReferenceCount<TextureAtlas>(path))
    assertEquals(listOf(storage.getIdentifier<Texture>(dependency)), storage.getDependencies<TextureAtlas>(path))
    // Atlas dependencies:
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertSame(asset.textures.first(), storage.get<Texture>(dependency))
    assertEquals(1, storage.getReferenceCount<Texture>(dependency))

    storage.dispose()
  }

  @Test
  fun `should unload TextureAtlas assets with dependencies`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.atlas"
    val dependency = "ktx/assets/async/texture.png"
    storage.testLoad<TextureAtlas>(path)

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
    val asset = storage.testLoad<Texture>(path)

    // Then:
    assertTrue(storage.isLoaded<Texture>(path))
    assertSame(asset, storage.get<Texture>(path))
    assertEquals(1, storage.getReferenceCount<Texture>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Texture>(path))

    storage.dispose()
  }

  @Test
  fun `should unload Texture assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"
    storage.testLoad<Texture>(path)

    // When:
    runBlocking { storage.unload<Texture>(path) }

    // Then:
    assertFalse(storage.isLoaded<Texture>(path))
    assertEquals(0, storage.getReferenceCount<Texture>(path))
  }

  @Test
  fun `should load Pixmap assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"

    // When:
    val asset = storage.testLoad<Pixmap>(path)

    // Then:
    assertTrue(storage.isLoaded<Pixmap>(path))
    assertSame(asset, storage.get<Pixmap>(path))
    assertEquals(1, storage.getReferenceCount<Pixmap>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Pixmap>(path))

    storage.dispose()
  }

  @Test
  fun `should unload Pixmap assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"
    storage.testLoad<Pixmap>(path)

    // When:
    runBlocking { storage.unload<Pixmap>(path) }

    // Then:
    assertFalse(storage.isLoaded<Pixmap>(path))
    assertEquals(0, storage.getReferenceCount<Pixmap>(path))
  }

  @Test
  fun `should load Skin assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.json"
    val atlas = "ktx/assets/async/skin.atlas"
    val texture = "ktx/assets/async/texture.png"

    // When:
    val asset = storage.testLoad<Skin>(path)

    // Then:
    assertTrue(storage.isLoaded<Skin>(path))
    assertSame(asset, storage.get<Skin>(path))
    assertNotNull(asset.get("default", Button.ButtonStyle::class.java))
    assertEquals(1, storage.getReferenceCount<Skin>(path))
    assertEquals(listOf(storage.getIdentifier<TextureAtlas>(atlas)), storage.getDependencies<Skin>(path))
    // Skin dependencies:
    assertTrue(storage.isLoaded<TextureAtlas>(atlas))
    assertEquals(1, storage.getReferenceCount<TextureAtlas>(atlas))
    assertSame(asset.atlas, storage.get<TextureAtlas>(atlas))
    assertEquals(listOf(storage.getIdentifier<Texture>(texture)), storage.getDependencies<TextureAtlas>(atlas))
    // Atlas dependencies:
    assertTrue(storage.isLoaded<Texture>(texture))
    assertSame(asset.atlas.textures.first(), storage.get<Texture>(texture))
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
    storage.testLoad<Skin>(path)

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
    val asset = storage.testLoad<I18NBundle>(path)

    // Then:
    assertTrue(storage.isLoaded<I18NBundle>(path))
    assertEquals("Value.", asset["key"])
    assertSame(asset, storage.get<I18NBundle>(path))
    assertEquals(1, storage.getReferenceCount<I18NBundle>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<I18NBundle>(path))

    storage.dispose()
  }

  @Test
  fun `should unload I18NBundle assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/i18n"
    storage.testLoad<I18NBundle>(path)

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
    val asset = storage.testLoad<ParticleEffect>(path)

    // Then:
    assertTrue(storage.isLoaded<ParticleEffect>(path))
    assertSame(asset, storage.get<ParticleEffect>(path))
    assertEquals(1, storage.getReferenceCount<ParticleEffect>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<ParticleEffect>(path))

    storage.dispose()
  }

  @Test
  fun `should unload ParticleEffect assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/particle.p2d"
    storage.testLoad<ParticleEffect>(path)

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
    val asset = storage.testLoad<ParticleEffect3D>(path)

    // Then:
    assertTrue(storage.isLoaded<ParticleEffect3D>(path))
    assertSame(asset, storage.get<ParticleEffect3D>(path))
    assertEquals(1, storage.getReferenceCount<ParticleEffect3D>(path))
    assertEquals(listOf(storage.getIdentifier<Texture>(dependency)), storage.getDependencies<ParticleEffect3D>(path))
    // Particle dependencies:
    assertTrue(storage.isLoaded<Texture>(dependency))
    assertNotNull(storage.get<Texture>(dependency))
    assertEquals(1, storage.getReferenceCount<Texture>(dependency))

    storage.dispose()
  }

  @Test
  fun `should unload ParticleEffect3D assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/particle.p3d"
    val dependency = "ktx/assets/async/texture.png"
    storage.testLoad<ParticleEffect3D>(path)

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
    val asset = storage.testLoad<Model>(path)

    // Then:
    assertTrue(storage.isLoaded<Model>(path))
    assertSame(asset, storage.get<Model>(path))
    assertEquals(1, storage.getReferenceCount<Model>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Model>(path))

    storage.dispose()
  }

  @Test
  fun `should unload OBJ Model assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.obj"
    storage.testLoad<Model>(path)

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
    val asset = storage.testLoad<Model>(path)

    // Then:
    assertTrue(storage.isLoaded<Model>(path))
    assertSame(asset, storage.get<Model>(path))
    assertEquals(1, storage.getReferenceCount<Model>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Model>(path))

    storage.dispose()
  }

  @Test
  fun `should unload G3DJ Model assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.g3dj"
    storage.testLoad<Model>(path)

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
    val asset = storage.testLoad<Model>(path)

    // Then:
    assertTrue(storage.isLoaded<Model>(path))
    assertSame(asset, storage.get<Model>(path))
    assertEquals(1, storage.getReferenceCount<Model>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Model>(path))

    storage.dispose()
  }

  @Test
  fun `should unload G3DB Model assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/model.g3db"
    storage.testLoad<Model>(path)

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
    val asset = storage.testLoad<ShaderProgram>(path)

    // Then:
    assertTrue(storage.isLoaded<ShaderProgram>(path))
    assertSame(asset, storage.get<ShaderProgram>(path))
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
    storage.testLoad<ShaderProgram>(path)

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
    val asset = storage.testLoad<Cubemap>(path)

    // Then:
    assertTrue(storage.isLoaded<Cubemap>(path))
    assertSame(asset, storage.get<Cubemap>(path))
    assertEquals(1, storage.getReferenceCount<Cubemap>(path))
    assertEquals(emptyList<String>(), storage.getDependencies<Cubemap>(path))

    storage.dispose()
  }

  @Test
  fun `should unload Cubemap assets`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/cubemap.zktx"
    storage.testLoad<Cubemap>(path)

    // When:
    runBlocking { storage.unload<Cubemap>(path) }

    // Then:
    assertFalse(storage.isLoaded<Cubemap>(path))
    assertEquals(0, storage.getReferenceCount<Cubemap>(path))
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
    assets.forEach {
      storage.testLoad(it.path, it.type, parameters = null)
      assertTrue(storage.isLoaded(it))
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
        storage[it]
      }
    }
  }

  // --- Behavior tests:

  @Test
  fun `should return same asset instance with subsequent load calls on loaded asset`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"
    val loaded = storage.testLoad<Texture>(path)

    // When:
    val assets = (1..10).map { storage.testLoad<Texture>(path) }

    // Then:
    assertEquals(11, storage.getReferenceCount<Texture>(path))
    assets.forEach { asset ->
      assertSame(loaded, asset)
    }
    checkProgress(storage, loaded = 1, warn = true)

    storage.dispose()
  }

  @Test
  fun `should obtain loaded asset with path`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"

    // When:
    storage.testLoad<String>(path)

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
    storage.testLoad<String>(identifier.path)

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
    storage.testLoad<String>(descriptor.fileName)

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
    storage.testLoad<String>(path)

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
    storage.testLoad<String>(path)

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
    storage.testLoad<String>(path)

    // When:
    runBlocking { storage.unload(identifier) }

    // Then:
    assertFalse(storage.isLoaded(identifier))
    assertEquals(0, storage.getReferenceCount(identifier))
    checkProgress(storage, total = 0, warn = true)
  }

  @Test
  fun `should allow to load multiple assets with different type and same path`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/texture.png"

    // When:
    storage.testLoad<Texture>(path)
    storage.testLoad<Pixmap>(path)

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
  fun `should increase references count and return the same asset when trying to load asset with same path`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/string.txt"
    val elements = IdentityHashMap<String, Boolean>()

    // When:
    repeat(3) {
      val asset = storage.testLoad<String>(path)
      elements[asset] = true
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
      storage.testLoad<Vector2>(path)
    }

    // Then:
    assertFalse(storage.contains<Vector2>(path))
    checkProgress(storage, total = 0)
  }

  @Test
  fun `should increase references counts of dependencies when loading same asset`() {
    // Given:
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val path = "ktx/assets/async/skin.json"
    val dependencies = arrayOf(
      storage.getIdentifier<TextureAtlas>("ktx/assets/async/skin.atlas"),
      storage.getIdentifier<Texture>("ktx/assets/async/texture.png")
    )
    val loadedAssets = IdentityHashMap<Skin, Boolean>()

    // When:
    repeat(3) {
      val asset = storage.testLoad<Skin>(path)
      loadedAssets[asset] = true
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
  fun `should handle loading exceptions`() {
    // Given:
    val loader = AssetStorageTest.FakeSyncLoader(
      onLoad = { throw IllegalStateException("Expected.") }
    )
    val storage = AssetStorage(useDefaultLoaders = false)
    storage.setLoader { loader }
    val path = "fake path"

    // When:
    shouldThrow<AssetLoadingException> {
      storage.testLoad<AssetStorageTest.FakeAsset>(path)
    }

    // Then: asset should still be in storage, but rethrowing original exception:
    assertTrue(storage.contains<AssetStorageTest.FakeAsset>(path))
    assertEquals(1, storage.getReferenceCount<AssetStorageTest.FakeAsset>(path))
    shouldThrow<AssetLoadingException> {
      storage.get<AssetStorageTest.FakeAsset>(path)
    }
    shouldThrow<AssetLoadingException> {
      storage.getOrNull<AssetStorageTest.FakeAsset>(path)
    }
    val reference = storage.getAsync<AssetStorageTest.FakeAsset>(path)
    shouldThrow<AssetLoadingException> {
      runBlocking { reference.await() }
    }
    checkProgress(storage, failed = 1, warn = true)
  }

  @Test
  fun `should handle asynchronous loading exceptions`() {
    // Given:
    val loader = AssetStorageTest.FakeAsyncLoader(
      onAsync = { throw IllegalStateException("Expected.") },
      onSync = {}
    )
    val storage = AssetStorage(useDefaultLoaders = false)
    storage.setLoader { loader }
    val path = "fake path"

    // When:
    shouldThrow<AssetLoadingException> {
      storage.testLoad<AssetStorageTest.FakeAsset>(path)
    }

    // Then: asset should still be in storage, but rethrowing original exception:
    assertTrue(storage.contains<AssetStorageTest.FakeAsset>(path))
    assertEquals(1, storage.getReferenceCount<AssetStorageTest.FakeAsset>(path))
    shouldThrow<AssetLoadingException> {
      storage.get<AssetStorageTest.FakeAsset>(path)
    }
    shouldThrow<AssetLoadingException> {
      storage.getOrNull<AssetStorageTest.FakeAsset>(path)
    }
    val reference = storage.getAsync<AssetStorageTest.FakeAsset>(path)
    shouldThrow<AssetLoadingException> {
      runBlocking { reference.await() }
    }
    checkProgress(storage, failed = 1, warn = true)
  }

  @Test
  fun `should handle synchronous loading exceptions`() {
    // Given:
    val loader = AssetStorageTest.FakeAsyncLoader(
      onAsync = { },
      onSync = { throw IllegalStateException("Expected.") }
    )
    val storage = AssetStorage(useDefaultLoaders = false)
    storage.setLoader { loader }
    val path = "fake path"

    // When:
    shouldThrow<AssetLoadingException> {
      storage.testLoad<AssetStorageTest.FakeAsset>(path)
    }

    // Then: asset should still be in storage, but rethrowing original exception:
    assertTrue(storage.contains<AssetStorageTest.FakeAsset>(path))
    assertEquals(1, storage.getReferenceCount<AssetStorageTest.FakeAsset>(path))
    shouldThrow<AssetLoadingException> {
      storage.get<AssetStorageTest.FakeAsset>(path)
    }
    shouldThrow<AssetLoadingException> {
      storage.getOrNull<AssetStorageTest.FakeAsset>(path)
    }
    val reference = storage.getAsync<AssetStorageTest.FakeAsset>(path)
    shouldThrow<AssetLoadingException> {
      runBlocking { reference.await() }
    }
    checkProgress(storage, failed = 1, warn = true)
  }

  @Test
  fun `should not fail to unload asset that was loaded exceptionally`() {
    // Given:
    val loader = AssetStorageTest.FakeSyncLoader(
      onLoad = { throw IllegalStateException("Expected.") }
    )
    val storage = AssetStorage(useDefaultLoaders = false)
    val path = "fake path"
    storage.setLoader { loader }
    storage.logger.level = Logger.NONE // Disposing exception will be logged.
    try {
      storage.testLoad<AssetStorageTest.FakeAsset>(path)
    } catch (exception: AssetLoadingException) {
      // Expected.
    }

    // When:
    val unloaded = runBlocking {
      storage.unload<AssetStorageTest.FakeAsset>(path)
    }

    // Then:
    assertTrue(unloaded)
    assertFalse(storage.contains<AssetStorageTest.FakeAsset>(path))
    assertEquals(0, storage.getReferenceCount<AssetStorageTest.FakeAsset>(path))
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
    if (System.getenv("TEST_PROFILE") != "ci") {
      Gdx.audio = OpenALAudio()
    }
  }

  @After
  override fun `exit LibGDX application`() {
    super.`exit LibGDX application`()
    (Gdx.audio as? OpenALAudio)?.dispose()
  }
}

/**
 * Performs asset loading tests with [AssetStorage.loadAsync] consuming [AssetDescriptor].
 */
class AssetStorageLoadingTestWithAssetDescriptorLoadAsync : AbstractAssetStorageLoadingTest() {
  override fun <T> AssetStorage.testLoad(
    path: String, type: Class<T>, parameters: AssetLoaderParameters<T>?
  ): T = runBlocking {
    loadAsync(AssetDescriptor(path, type, parameters)).await()
  }
}

/**
 * Performs asset loading tests with [AssetStorage.loadAsync] consuming [Identifier].
 */
class AssetStorageLoadingTestWithIdentifierLoadAsync : AbstractAssetStorageLoadingTest() {
  override fun <T> AssetStorage.testLoad(
    path: String, type: Class<T>, parameters: AssetLoaderParameters<T>?
  ): T = runBlocking {
    loadAsync(Identifier(path, type), parameters).await()
  }
}

/**
 * Performs asset loading tests with [AssetStorage.load] consuming [AssetDescriptor].
 */
class AssetStorageLoadingTestWithAssetDescriptorLoad : AbstractAssetStorageLoadingTest() {
  override fun <T> AssetStorage.testLoad(
    path: String, type: Class<T>, parameters: AssetLoaderParameters<T>?
  ): T = runBlocking {
    load(AssetDescriptor(path, type, parameters))
  }
}

/**
 * Performs asset loading tests with [AssetStorage.load] consuming [Identifier].
 */
class AssetStorageLoadingTestWithIdentifierLoad : AbstractAssetStorageLoadingTest() {
  override fun <T> AssetStorage.testLoad(
    path: String, type: Class<T>, parameters: AssetLoaderParameters<T>?
  ): T = runBlocking {
    load(Identifier(path, type), parameters)
  }
}

/**
 * Performs asset loading tests with [AssetStorage.loadSync] consuming [AssetDescriptor].
 */
class AssetStorageLoadingTestWithAssetDescriptorLoadSync : AbstractAssetStorageLoadingTest() {
  override fun <T> AssetStorage.testLoad(
    path: String, type: Class<T>, parameters: AssetLoaderParameters<T>?
  ): T = loadSync(AssetDescriptor(path, type, parameters))
}

/**
 * Performs asset loading tests with [AssetStorage.loadSync] consuming [Identifier].
 */
class AssetStorageLoadingTestWithIdentifierLoadSync : AbstractAssetStorageLoadingTest() {
  override fun <T> AssetStorage.testLoad(
    path: String, type: Class<T>, parameters: AssetLoaderParameters<T>?
  ): T = loadSync(Identifier(path, type), parameters)
}
