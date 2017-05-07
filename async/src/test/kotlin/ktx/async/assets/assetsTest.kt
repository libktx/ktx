package ktx.async.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.backends.lwjgl.LwjglFiles
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.async.AsyncExecutor
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import ktx.async.`coroutine test`
import ktx.async.`destroy coroutines context`
import ktx.async.assets.TextAssetLoader.TextAssetLoaderParameters
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Test
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect as ParticleEffect3d

/**
 * Tests [AssetStorage]: coroutines-based asset manager.
 */
class AssetStorageTest {
  @Test
  fun `should load text assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<String>("ktx/async/assets/string.txt")

      assertEquals("Content.", asset)
      assertTrue(storage.isLoaded("ktx/async/assets/string.txt"))
      assertSame(asset, storage.get<String>("ktx/async/assets/string.txt"))
    }
  }

  @Test
  fun `should load text assets with parameters`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load("ktx/async/assets/string.txt", parameters = TextAssetLoaderParameters("UTF-8"))

      assertEquals("Content.", asset)
      assertTrue(storage.isLoaded("ktx/async/assets/string.txt"))
      assertSame(asset, storage.get<String>("ktx/async/assets/string.txt"))
    }
  }

  @Test
  fun `should load text assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/string.txt", String::class.java,
        TextAssetLoaderParameters(charset = "UTF-8"))

    ktxAsync {
      val asset = storage.load(descriptor)

      assertEquals("Content.", asset)
      assertTrue(storage.isLoaded("ktx/async/assets/string.txt"))
      assertSame(asset, storage.get<String>("ktx/async/assets/string.txt"))
    }
  }

  @Test
  fun `should return loaded asset if trying to load the same asset`()
      = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<String>("ktx/async/assets/string.txt")

      assertSame(asset, storage.load<String>("ktx/async/assets/string.txt"))
    }
  }

  @Test
  fun `should return loaded asset if trying to load the same asset with descriptor`()
      = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/string.txt", String::class.java,
        TextAssetLoaderParameters(charset = "UTF-8"))

    ktxAsync {
      val asset = storage.load(descriptor)

      assertSame(asset, storage.load(descriptor))
    }
  }

  @Test
  fun `should load BitmapFont assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<BitmapFont>("com/badlogic/gdx/utils/arial-15.fnt")

      assertTrue(asset is BitmapFont)
      assertTrue(storage.isLoaded("com/badlogic/gdx/utils/arial-15.fnt"))
      assertSame(asset, storage.get<BitmapFont>("com/badlogic/gdx/utils/arial-15.fnt"))
      // Font dependencies:
      assertTrue(storage.isLoaded("com/badlogic/gdx/utils/arial-15.png"))
      assertNotNull(storage.get<Texture>("com/badlogic/gdx/utils/arial-15.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should load BitmapFont assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("com/badlogic/gdx/utils/arial-15.fnt", BitmapFont::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is BitmapFont)
      assertTrue(storage.isLoaded("com/badlogic/gdx/utils/arial-15.fnt"))
      assertSame(asset, storage.get<BitmapFont>("com/badlogic/gdx/utils/arial-15.fnt"))
      // Font dependencies:
      assertTrue(storage.isLoaded("com/badlogic/gdx/utils/arial-15.png"))
      assertNotNull(storage.get<Texture>("com/badlogic/gdx/utils/arial-15.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should load Music assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<Music>("ktx/async/assets/sound.ogg")

      assertTrue(asset is Music)
      assertTrue(storage.isLoaded("ktx/async/assets/sound.ogg"))
      assertSame(asset, storage.get<Music>("ktx/async/assets/sound.ogg"))
      storage.dispose()
    }
  }

  @Test
  fun `should load Music assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/sound.ogg", Music::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is Music)
      assertTrue(storage.isLoaded("ktx/async/assets/sound.ogg"))
      assertSame(asset, storage.get<Music>("ktx/async/assets/sound.ogg"))
      storage.dispose()
    }
  }

  @Test
  fun `should load Sound assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<Sound>("ktx/async/assets/sound.ogg")

      assertTrue(asset is Sound)
      assertTrue(storage.isLoaded("ktx/async/assets/sound.ogg"))
      assertSame(asset, storage.get<Sound>("ktx/async/assets/sound.ogg"))
      storage.dispose()
    }
  }

  @Test
  fun `should load Sound assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/sound.ogg", Sound::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is Sound)
      assertTrue(storage.isLoaded("ktx/async/assets/sound.ogg"))
      assertSame(asset, storage.get<Sound>("ktx/async/assets/sound.ogg"))
      storage.dispose()
    }
  }

  @Test
  fun `should load TextureAtlas assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<TextureAtlas>("ktx/async/assets/skin.atlas")

      assertTrue(asset is TextureAtlas)
      assertTrue(storage.isLoaded("ktx/async/assets/skin.atlas"))
      assertSame(asset, storage.get<TextureAtlas>("ktx/async/assets/skin.atlas"))
      // Atlas dependencies:
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertNotNull(storage.get<Texture>("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should load TextureAtlas assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/skin.atlas", TextureAtlas::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is TextureAtlas)
      assertTrue(storage.isLoaded("ktx/async/assets/skin.atlas"))
      assertSame(asset, storage.get<TextureAtlas>("ktx/async/assets/skin.atlas"))
      // Atlas dependencies:
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertNotNull(storage.get<Texture>("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should load Texture assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<Texture>("ktx/async/assets/texture.png")

      assertTrue(asset is Texture)
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertSame(asset, storage.get<Texture>("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should load Texture assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/texture.png", Texture::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is Texture)
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertSame(asset, storage.get<Texture>("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should load Pixmap assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<Pixmap>("ktx/async/assets/texture.png")

      assertTrue(asset is Pixmap)
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertSame(asset, storage.get<Pixmap>("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should load Pixmap assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/texture.png", Pixmap::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is Pixmap)
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertSame(asset, storage.get<Pixmap>("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should load Skin assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<Skin>("ktx/async/assets/skin.json")

      assertTrue(asset is Skin)
      assertNotNull(asset.get("default", ButtonStyle::class.java))
      assertTrue(storage.isLoaded("ktx/async/assets/skin.json"))
      assertSame(asset, storage.get<Skin>("ktx/async/assets/skin.json"))

      // Skin dependencies:
      assertTrue(storage.isLoaded("ktx/async/assets/skin.atlas"))
      val atlas = storage.get<TextureAtlas>("ktx/async/assets/skin.atlas")
      assertNotNull(atlas)
      assertNotNull(atlas?.findRegion("button"))

      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertNotNull(storage.get<Texture>("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should load Skin assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/skin.json", Skin::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is Skin)
      assertNotNull(asset.get("default", ButtonStyle::class.java))
      assertTrue(storage.isLoaded("ktx/async/assets/skin.json"))
      assertSame(asset, storage.get<Skin>("ktx/async/assets/skin.json"))

      // Skin dependencies:
      assertTrue(storage.isLoaded("ktx/async/assets/skin.atlas"))
      val atlas = storage.get<TextureAtlas>("ktx/async/assets/skin.atlas")
      assertNotNull(atlas)
      assertNotNull(atlas?.findRegion("button"))

      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertNotNull(storage.get<Texture>("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should load I18NBundle assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<I18NBundle>("ktx/async/assets/i18n")

      assertTrue(asset is I18NBundle)
      assertEquals("Value.", asset["key"])
      assertTrue(storage.isLoaded("ktx/async/assets/i18n"))
      assertSame(asset, storage.get<I18NBundle>("ktx/async/assets/i18n"))
      storage.dispose()
    }
  }

  @Test
  fun `should load I18NBundle assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/i18n", I18NBundle::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is I18NBundle)
      assertEquals("Value.", asset["key"])
      assertTrue(storage.isLoaded("ktx/async/assets/i18n"))
      assertSame(asset, storage.get<I18NBundle>("ktx/async/assets/i18n"))
      storage.dispose()
    }
  }

  @Test
  fun `should load ParticleEffect assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<ParticleEffect>("ktx/async/assets/particle.p2d")

      assertTrue(asset is ParticleEffect)
      assertTrue(storage.isLoaded("ktx/async/assets/particle.p2d"))
      assertSame(asset, storage.get<ParticleEffect>("ktx/async/assets/particle.p2d"))
      storage.dispose()
    }
  }

  @Test
  fun `should load ParticleEffect assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/particle.p2d", ParticleEffect::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is ParticleEffect)
      assertTrue(storage.isLoaded("ktx/async/assets/particle.p2d"))
      assertSame(asset, storage.get<ParticleEffect>("ktx/async/assets/particle.p2d"))
      storage.dispose()
    }
  }

  @Test
  fun `should load 3D ParticleEffect assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<ParticleEffect3d>("ktx/async/assets/particle.p3d")

      assertTrue(asset is ParticleEffect3d)
      assertTrue(storage.isLoaded("ktx/async/assets/particle.p3d"))
      assertSame(asset, storage.get<ParticleEffect3d>("ktx/async/assets/particle.p3d"))
      // Effect dependencies:
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertNotNull(storage.get<Texture>("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should load 3D ParticleEffect assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/particle.p3d", ParticleEffect3d::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is ParticleEffect3d)
      assertTrue(storage.isLoaded("ktx/async/assets/particle.p3d"))
      assertSame(asset, storage.get<ParticleEffect3d>("ktx/async/assets/particle.p3d"))
      // Effect dependencies:
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertNotNull(storage.get<Texture>("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should load OBJ Model assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<Model>("ktx/async/assets/model.obj")

      assertTrue(asset is Model)
      assertTrue(storage.isLoaded("ktx/async/assets/model.obj"))
      assertSame(asset, storage.get<Model>("ktx/async/assets/model.obj"))
      storage.dispose()
    }
  }

  @Test
  fun `should load OBJ Model assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/model.obj", Model::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is Model)
      assertTrue(storage.isLoaded("ktx/async/assets/model.obj"))
      assertSame(asset, storage.get<Model>("ktx/async/assets/model.obj"))
      storage.dispose()
    }
  }

  @Test
  fun `should load G3DJ Model assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<Model>("ktx/async/assets/model.g3dj")

      assertTrue(asset is Model)
      assertTrue(storage.isLoaded("ktx/async/assets/model.g3dj"))
      assertSame(asset, storage.get<Model>("ktx/async/assets/model.g3dj"))
      storage.dispose()
    }
  }

  @Test
  fun `should load G3DJ Model assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/model.g3dj", Model::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is Model)
      assertTrue(storage.isLoaded("ktx/async/assets/model.g3dj"))
      assertSame(asset, storage.get<Model>("ktx/async/assets/model.g3dj"))
      storage.dispose()
    }
  }

  @Test
  fun `should load G3DB Model assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<Model>("ktx/async/assets/model.g3db")

      assertTrue(asset is Model)
      assertTrue(storage.isLoaded("ktx/async/assets/model.g3db"))
      assertSame(asset, storage.get<Model>("ktx/async/assets/model.g3db"))
      storage.dispose()
    }
  }

  @Test
  fun `should load G3DB Model assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/model.g3db", Model::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is Model)
      assertTrue(storage.isLoaded("ktx/async/assets/model.g3db"))
      assertSame(asset, storage.get<Model>("ktx/async/assets/model.g3db"))
      storage.dispose()
    }
  }

  @Test
  fun `should load ShaderProgram assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<ShaderProgram>("ktx/async/assets/shader.frag")

      assertTrue(asset is ShaderProgram)
      assertTrue(storage.isLoaded("ktx/async/assets/shader.frag"))
      assertSame(asset, storage.get<ShaderProgram>("ktx/async/assets/shader.frag"))
      storage.dispose()
    }
  }

  @Test
  fun `should load ShaderProgram assets with descriptor`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/shader.vert", ShaderProgram::class.java)

    ktxAsync {
      val asset = storage.load(descriptor)

      assertTrue(asset is ShaderProgram)
      assertTrue(storage.isLoaded("ktx/async/assets/shader.vert"))
      assertSame(asset, storage.get<ShaderProgram>("ktx/async/assets/shader.vert"))
      storage.dispose()
    }
  }

  @Test
  fun `should create AssetDescriptor`() {
    val storage = assetStorage()

    val descriptor = storage.getAssetDescriptor<String>("ktx/async/assets/string.txt")

    assertEquals("ktx/async/assets/string.txt", descriptor.fileName)
    assertEquals("ktx/async/assets/string.txt", descriptor.file.path())
    assertEquals(String::class.java, descriptor.type)
    assertNull(descriptor.params)
  }

  @Test
  fun `should create AssetDescriptor with loading parameters`() {
    val storage = assetStorage()
    val parameters = mock<AssetLoaderParameters<String>>()

    val descriptor = storage.getAssetDescriptor("ktx/async/assets/string.txt", parameters)

    assertEquals("ktx/async/assets/string.txt", descriptor.fileName)
    assertEquals("ktx/async/assets/string.txt", descriptor.file.path())
    assertEquals(String::class.java, descriptor.type)
    assertSame(parameters, descriptor.params)
  }

  @Test
  fun `should return loaded asset`() {
    val storage = assetStorage()
    val asset = Any()
    storage.assets.put("test", asset)

    val result = storage.get<Any>("test")

    assertSame(asset, result)
  }

  @Test
  fun `should not return loaded asset if given type is inconsistent with the loaded asset`() {
    val storage = assetStorage()
    storage.assets.put("test", Any())

    val result = storage.get<String>("test")

    assertNull(result)
  }

  @Test
  fun `should not return absent asset`() {
    val storage = assetStorage()

    assertNull(storage.get<String>("test"))
  }

  @Test
  fun `should return loaded asset given alternative`() {
    val storage = assetStorage()
    val asset = Any()
    val alternative = Any()
    storage.assets.put("test", asset)

    val result = storage.getOrElse("test", alternative)

    assertSame(asset, result)
    assertNotSame(alternative, result)
  }

  @Test
  fun `should return alternative if given type is inconsistent with the loaded asset`() {
    val storage = assetStorage()
    val asset = Any()
    val alternative = "alternative"
    storage.assets.put("test", asset)

    val result = storage.getOrElse("test", alternative) // Requesting a String, actual asset is Any.

    assertNotSame(asset, result)
    assertSame(alternative, result)
  }

  @Test
  fun `should return alternative if asset is not loaded`() {
    val storage = assetStorage()
    val alternative = Any()

    assertSame(alternative, storage.getOrElse("test", alternative))
  }

  @Test
  fun `should normalize file paths`() {
    // Should match AssetDescriptor logic.
    assetStorage().apply {
      assertEquals("path.txt", "path.txt".normalizePath())
      assertEquals("/path.txt", "\\path.txt".normalizePath())
      assertEquals("dir/path.txt", "dir\\path.txt".normalizePath())
      assertEquals("home/dir/path.txt", "home\\dir\\path.txt".normalizePath())
      assertEquals("/home/dir/dir/", "\\home\\dir\\dir\\".normalizePath())
      assertEquals(AssetDescriptor("home\\dir\\path.txt", String::class.java).fileName,
          "home\\dir\\path.txt".normalizePath())
    }
  }

  @Test
  fun `should allow to manually add assets to the storage`() {
    val storage = assetStorage()
    val asset = Any()

    storage.add("test", asset)

    assertTrue(storage.isLoaded("test"))
    assertSame(asset, storage["test"])
    assertSame(asset, storage.assets["test"])
  }

  @Test
  fun `should not throw an exception when manually adding the same asset with same path to the storage`() {
    val storage = assetStorage()
    val asset = Any()
    storage.assets.put("test", asset)

    storage.add("test", asset)

    assertTrue(storage.isLoaded("test"))
    assertSame(asset, storage["test"])
    assertSame(asset, storage.assets["test"])
  }

  @Test(expected = AssetStorageException::class)
  fun `should throw an exception when manually adding a different asset with same path to the storage`() {
    val storage = assetStorage()
    val previous = Any()
    storage.assets.put("test", previous)
    val asset = Any()

    storage.add("test", asset)
  }

  @Test
  fun `should check if asset is loaded`() {
    val storage = assetStorage()
    storage.assets.put("test", "asset")

    assertTrue(storage.isLoaded("test"))
    assertFalse(storage.isLoaded("absent"))
  }

  @Test
  fun `should unload asset`() {
    val storage = assetStorage()
    val asset = mock<Disposable>()
    storage.assets.put("test", asset)

    storage.unload("test")

    assertFalse(storage.assets.containsKey("test"))
    verify(asset).dispose()
  }

  @Test
  fun `should not throw an exception when trying to unload absent asset`() {
    val storage = assetStorage()

    storage.unload("test")
  }

  @Test
  fun `should remove asset without disposing`() {
    val storage = assetStorage()
    val asset = mock<Disposable>()
    storage.assets.put("test", asset)

    val removed = storage.remove<Disposable>("test")

    assertSame(asset, removed)
    assertFalse(storage.assets.containsKey("test"))
    verify(asset, never()).dispose()
  }

  @Test
  fun `should remove asset without disposing, but do not return it if its type is inconsistent with the request`() {
    val storage = assetStorage()
    val asset = mock<Disposable>()
    storage.assets.put("test", asset)

    val removed = storage.remove<String>("test") // Requesting a String, actual asset is Disposable.

    assertNull(removed)
    assertFalse(storage.assets.containsKey("test"))
    verify(asset, never()).dispose()
  }

  @Test
  fun `should return null if trying to remove absent asset`() {
    val storage = assetStorage()

    val removed = storage.remove<String>("test")

    assertNull(removed)
  }

  @Test
  fun `should register asset loader`() {
    val storage = assetStorage()
    val loader = mock<SynchronousLoader<Int>>()

    storage.setLoader(loader)

    assertSame(loader, storage.getLoader<Int>())
  }

  @Test
  fun `should register asset loader with suffix`() {
    val storage = assetStorage()
    val loader = mock<SynchronousLoader<Int>>()

    storage.setLoader(loader, suffix = ".txt")

    assertSame(loader, storage.getLoader<Int>("file.txt"))
    assertNull(storage.getLoader<Int>("file.md"))
  }

  @Test
  fun `should return null if asset loader is not available`() {
    val storage = assetStorage()

    assertNull(storage.getLoader<Int>())
  }

  @Test(expected = AssetStorageException::class)
  fun `should reject invalid asset loader implementations`() {
    val storage = assetStorage()
    val invalidLoader = mock<Loader<Int>>() // Does not extend Synchronous/AsynchronousAssetLoader.

    storage.setLoader(invalidLoader)
  }

  @Test
  fun `should dispose of all assets`() {
    val storage = assetStorage()
    val disposables = (1..5).map { it.toString() to mock<Disposable>() }
        .onEach { (path, asset) -> storage.assets.put(path, asset) }
        .map { (_, asset) -> asset }.toList()
    storage.assets.put("non-disposable", "Asset.")

    storage.dispose()

    assertEquals(0, storage.assets.size)
    disposables.forEach {
      verify(it).dispose()
    }
  }

  @Test
  fun `should dispose of all assets with optional error handling`() {
    val storage = assetStorage()
    val disposables = (1..5).map { it.toString() to mock<Disposable>() }
        .onEach { (path, asset) -> storage.assets.put(path, asset) }
        .map { (_, asset) -> asset }.toList()
    storage.assets.put("non-disposable", "Asset.")

    storage.dispose { path, error ->
      fail("Should not catch any exceptions, yet $error was caught during $path disposing.")
    }

    assertEquals(0, storage.assets.size)
    disposables.forEach {
      verify(it).dispose()
    }
  }

  @Test
  fun `should catch errors during disposing`() {
    val storage = assetStorage()
    val validAsset = mock<Disposable>()
    val brokenAsset = mock<Disposable> {
      on(it.dispose()) doThrow GdxRuntimeException("Expected.")
    }
    storage.assets.put("broken", brokenAsset)
    storage.assets.put("valid", validAsset)

    storage.dispose { path, error ->
      assertEquals("broken", path)
      assertEquals("Expected.", error.message)
      assertTrue(error is GdxRuntimeException)
    }

    verify(brokenAsset).dispose()
    verify(validAsset).dispose()
  }

  private fun assetStorage() = AssetStorage(
      fileResolver = ClasspathFileHandleResolver(),
      executor = AsyncExecutor(1),
      useDefaultLoaders = false)

  @After
  fun dispose() {
    `destroy coroutines context`()
  }

  companion object {
    @JvmStatic
    @BeforeClass
    fun `create LibGDX application`() {
      // Necessary for LibGDX asset loaders to work.
      LwjglNativesLoader.load()

      Gdx.audio = OpenALAudio()
      Gdx.files = LwjglFiles()
      Gdx.graphics = mock()
      Gdx.gl20 = mock()
      Gdx.app = mock()
      Gdx.gl = mock()
    }

    @JvmStatic
    @AfterClass
    fun `dispose LibGDX application`() {
      (Gdx.audio as OpenALAudio).dispose()
      Gdx.graphics = null
      Gdx.audio = null
      Gdx.files = null
      Gdx.gl20 = null
      Gdx.app = null
      Gdx.gl = null
    }
  }
}
