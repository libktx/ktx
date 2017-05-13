package ktx.async.assets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader
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
import com.nhaarman.mockito_kotlin.*
import ktx.async.`coroutine test`
import ktx.async.`destroy coroutines context`
import ktx.async.assets.TextAssetLoader.TextAssetLoaderParameters
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Test
import java.util.Collections
import java.util.IdentityHashMap
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicInteger
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect as ParticleEffect3d
import com.badlogic.gdx.utils.Array as GdxArray

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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/string.txt"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/string.txt"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/string.txt"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/string.txt"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/string.txt"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/string.txt"))
    }
  }

  @Test
  fun `should load text assets with descriptor as a dependency`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/string.txt", String::class.java,
        TextAssetLoaderParameters(charset = "UTF-8"))

    ktxAsync {
      val asset = storage.load(descriptor, isDependency = true)

      assertEquals("Content.", asset)
      assertTrue(storage.isLoaded("ktx/async/assets/string.txt"))
      assertSame(asset, storage.get<String>("ktx/async/assets/string.txt"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/string.txt")) // Reference count not incremented.
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/string.txt"))
    }
  }

  @Test
  fun `should increase references count and return loaded asset if trying to load the same asset`()
      = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.load<String>("ktx/async/assets/string.txt")

      assertEquals(1, storage.getReferencesCount("ktx/async/assets/string.txt"))
      assertSame(asset, storage.load<String>("ktx/async/assets/string.txt"))
      assertEquals(2, storage.getReferencesCount("ktx/async/assets/string.txt"))
    }
  }

  @Test
  fun `should increase references count return loaded asset if trying to load the same asset with descriptor`()
      = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/string.txt", String::class.java,
        TextAssetLoaderParameters(charset = "UTF-8"))

    ktxAsync {
      val asset = storage.load(descriptor)

      assertEquals(1, storage.getReferencesCount("ktx/async/assets/string.txt"))
      assertSame(asset, storage.load(descriptor))
      assertEquals(2, storage.getReferencesCount("ktx/async/assets/string.txt"))
    }
  }

  @Test
  fun `should not increase references count return loaded asset if trying to load the same asset with descriptor as dependency`()
      = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val descriptor = AssetDescriptor("ktx/async/assets/string.txt", String::class.java,
        TextAssetLoaderParameters(charset = "UTF-8"))

    ktxAsync {
      val asset = storage.load(descriptor)

      assertEquals(1, storage.getReferencesCount("ktx/async/assets/string.txt"))
      assertSame(asset, storage.load(descriptor, isDependency = true))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/string.txt"))
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
      assertEquals(1, storage.getReferencesCount("com/badlogic/gdx/utils/arial-15.fnt"))
      assertEquals(listOf("com/badlogic/gdx/utils/arial-15.png"),
          storage.getDependencies("com/badlogic/gdx/utils/arial-15.fnt"))
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
      assertEquals(1, storage.getReferencesCount("com/badlogic/gdx/utils/arial-15.fnt"))
      assertEquals(listOf("com/badlogic/gdx/utils/arial-15.png"),
          storage.getDependencies("com/badlogic/gdx/utils/arial-15.fnt"))
      // Font dependencies:
      assertTrue(storage.isLoaded("com/badlogic/gdx/utils/arial-15.png"))
      assertNotNull(storage.get<Texture>("com/badlogic/gdx/utils/arial-15.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload BitmapFont assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<BitmapFont>("com/badlogic/gdx/utils/arial-15.fnt")

      storage.unload("com/badlogic/gdx/utils/arial-15.fnt")

      assertFalse(storage.isLoaded("com/badlogic/gdx/utils/arial-15.fnt"))
      assertEquals(0, storage.getReferencesCount("com/badlogic/gdx/utils/arial-15.fnt"))
      assertEquals(emptyList<String>(), storage.getDependencies("com/badlogic/gdx/utils/arial-15.fnt"))
      assertFalse(storage.isLoaded("com/badlogic/gdx/utils/arial-15.png"))
      assertEquals(0, storage.getReferencesCount("com/badlogic/gdx/utils/arial-15.png"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/sound.ogg"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/sound.ogg"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/sound.ogg"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/sound.ogg"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload Music assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<Music>("ktx/async/assets/sound.ogg")

      storage.unload("ktx/async/assets/sound.ogg")

      assertFalse(storage.isLoaded("ktx/async/assets/sound.ogg"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/sound.ogg"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/sound.ogg"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/sound.ogg"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/sound.ogg"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/sound.ogg"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/sound.ogg"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload Sound assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<Sound>("ktx/async/assets/sound.ogg")

      storage.unload("ktx/async/assets/sound.ogg")

      assertFalse(storage.isLoaded("ktx/async/assets/sound.ogg"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/sound.ogg"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/sound.ogg"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/skin.atlas"))
      assertEquals(listOf("ktx/async/assets/texture.png"), storage.getDependencies("ktx/async/assets/skin.atlas"))
      // Atlas dependencies:
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertNotNull(storage.get<Texture>("ktx/async/assets/texture.png"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/skin.atlas"))
      assertEquals(listOf("ktx/async/assets/texture.png"), storage.getDependencies("ktx/async/assets/skin.atlas"))
      // Atlas dependencies:
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertNotNull(storage.get<Texture>("ktx/async/assets/texture.png"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload TextureAtlas assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<TextureAtlas>("ktx/async/assets/skin.atlas")

      storage.unload("ktx/async/assets/skin.atlas")

      assertFalse(storage.isLoaded("ktx/async/assets/skin.atlas"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/skin.atlas"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/skin.atlas"))
      assertFalse(storage.isLoaded("ktx/async/assets/texture.png"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/texture.png"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/texture.png"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload Texture assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<Texture>("ktx/async/assets/texture.png")

      storage.unload("ktx/async/assets/texture.png")

      assertFalse(storage.isLoaded("ktx/async/assets/texture.png"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/texture.png"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/texture.png"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/texture.png"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload Pixmap assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<Pixmap>("ktx/async/assets/texture.png")

      storage.unload("ktx/async/assets/texture.png")

      assertFalse(storage.isLoaded("ktx/async/assets/texture.png"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/texture.png"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/texture.png"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/skin.json"))
      assertEquals(listOf("ktx/async/assets/skin.atlas"), storage.getDependencies("ktx/async/assets/skin.json"))

      // Skin dependencies:
      assertTrue(storage.isLoaded("ktx/async/assets/skin.atlas"))
      val atlas = storage.get<TextureAtlas>("ktx/async/assets/skin.atlas")
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/skin.atlas"))
      assertEquals(listOf("ktx/async/assets/texture.png"), storage.getDependencies("ktx/async/assets/skin.atlas"))
      assertNotNull(atlas)
      assertNotNull(atlas?.findRegion("button"))

      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertNotNull(storage.get<Texture>("ktx/async/assets/texture.png"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/skin.json"))
      assertEquals(listOf("ktx/async/assets/skin.atlas"), storage.getDependencies("ktx/async/assets/skin.json"))

      // Skin dependencies:
      assertTrue(storage.isLoaded("ktx/async/assets/skin.atlas"))
      val atlas = storage.get<TextureAtlas>("ktx/async/assets/skin.atlas")
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/skin.atlas"))
      assertEquals(listOf("ktx/async/assets/texture.png"), storage.getDependencies("ktx/async/assets/skin.atlas"))
      assertNotNull(atlas)
      assertNotNull(atlas?.findRegion("button"))

      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertNotNull(storage.get<Texture>("ktx/async/assets/texture.png"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload Skin assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<Skin>("ktx/async/assets/skin.json")

      storage.unload("ktx/async/assets/skin.json")

      assertFalse(storage.isLoaded("ktx/async/assets/skin.json"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/skin.json"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/skin.json"))
      assertFalse(storage.isLoaded("ktx/async/assets/skin.atlas"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/skin.atlas"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/skin.atlas"))
      assertFalse(storage.isLoaded("ktx/async/assets/texture.png"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/texture.png"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/i18n"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/i18n"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/i18n"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/i18n"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload I18NBundle assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<I18NBundle>("ktx/async/assets/i18n")

      storage.unload("ktx/async/assets/i18n")

      assertFalse(storage.isLoaded("ktx/async/assets/i18n"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/i18n"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/i18n"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/particle.p2d"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/particle.p2d"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/particle.p2d"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/particle.p2d"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload ParticleEffect assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<ParticleEffect>("ktx/async/assets/particle.p2d")

      storage.unload("ktx/async/assets/particle.p2d")

      assertFalse(storage.isLoaded("ktx/async/assets/particle.p2d"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/particle.p2d"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/particle.p2d"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/particle.p3d"))
      assertEquals(listOf("ktx/async/assets/texture.png"), storage.getDependencies("ktx/async/assets/particle.p3d"))
      // Effect dependencies:
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertNotNull(storage.get<Texture>("ktx/async/assets/texture.png"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/particle.p3d"))
      assertEquals(listOf("ktx/async/assets/texture.png"), storage.getDependencies("ktx/async/assets/particle.p3d"))
      // Effect dependencies:
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertNotNull(storage.get<Texture>("ktx/async/assets/texture.png"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload 3D ParticleEffect assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<ParticleEffect3d>("ktx/async/assets/particle.p3d")

      storage.unload("ktx/async/assets/particle.p3d")

      assertFalse(storage.isLoaded("ktx/async/assets/particle.p3d"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/particle.p3d"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/particle.p3d"))
      assertFalse(storage.isLoaded("ktx/async/assets/texture.png"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/texture.png"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/model.obj"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/model.obj"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/model.obj"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/model.obj"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload OBJ Model assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<Model>("ktx/async/assets/model.obj")

      storage.unload("ktx/async/assets/model.obj")

      assertFalse(storage.isLoaded("ktx/async/assets/model.obj"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/model.obj"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/model.obj"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/model.g3dj"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/model.g3dj"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/model.g3dj"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/model.g3dj"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload G3DJ Model assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<Model>("ktx/async/assets/model.g3dj")

      storage.unload("ktx/async/assets/model.g3dj")

      assertFalse(storage.isLoaded("ktx/async/assets/model.g3dj"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/model.g3dj"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/model.g3dj"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/model.g3db"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/model.g3db"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/model.g3db"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/model.g3db"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload G3DB Model assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<Model>("ktx/async/assets/model.g3db")

      storage.unload("ktx/async/assets/model.g3db")

      assertFalse(storage.isLoaded("ktx/async/assets/model.g3db"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/model.g3db"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/model.g3db"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/shader.frag"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/shader.frag"))
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
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/shader.vert"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/shader.vert"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload ShaderProgram assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<ShaderProgram>("ktx/async/assets/shader.frag")

      storage.unload("ktx/async/assets/shader.frag")

      assertFalse(storage.isLoaded("ktx/async/assets/shader.frag"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/shader.frag"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/shader.frag"))
    }
  }

  @Test
  fun `should load JSON assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.loadJson<JsonExample>("ktx/async/assets/object.json")

      assertTrue(asset is JsonExample)
      assertEquals(10, asset.testInt)
      assertEquals("Content.", asset.testString)
      assertTrue(storage.isLoaded("ktx/async/assets/object.json"))
      assertSame(asset, storage.get<JsonExample>("ktx/async/assets/object.json"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/object.json"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/object.json"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload JSON assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.loadJson<JsonExample>("ktx/async/assets/object.json")

      storage.unload("ktx/async/assets/object.json")

      assertFalse(storage.isLoaded("ktx/async/assets/object.json"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/object.json"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/object.json"))
    }
  }

  @Test
  fun `should return loaded JSON asset if trying to load the same asset`()
      = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.loadJson<JsonExample>("ktx/async/assets/object.json")

      assertEquals(1, storage.getReferencesCount("ktx/async/assets/object.json"))
      assertSame(asset, storage.loadJson<JsonExample>("ktx/async/assets/object.json"))
      assertEquals(2, storage.getReferencesCount("ktx/async/assets/object.json"))
    }
  }

  @Test
  fun `should load JSON collection assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.loadJsonCollection<GdxArray<JsonExample>, JsonExample>("ktx/async/assets/collection.json")

      assertTrue(asset is GdxArray<*>)
      assertEquals(2, asset.size)
      assertTrue(storage.isLoaded("ktx/async/assets/collection.json"))
      assertSame(asset, storage.get<GdxArray<JsonExample>>("ktx/async/assets/collection.json"))
      asset[0].apply {
        assertTrue(this is JsonExample)
        assertEquals(10, testInt)
        assertEquals("Content.", testString)
      }
      asset[1].apply {
        assertTrue(this is JsonExample)
        assertEquals(20, testInt)
        assertEquals("Test.", testString)
      }
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/collection.json"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/collection.json"))
      storage.dispose()
    }
  }

  @Test
  fun `should unload JSON collection assets`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.loadJsonCollection<GdxArray<JsonExample>, JsonExample>("ktx/async/assets/collection.json")

      storage.unload("ktx/async/assets/collection.json")

      assertFalse(storage.isLoaded("ktx/async/assets/collection.json"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/collection.json"))
      assertEquals(emptyList<String>(), storage.getDependencies("ktx/async/assets/collection.json"))
    }
  }

  @Test
  fun `should return loaded JSON collection asset if trying to load the same asset`()
      = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      val asset = storage.loadJsonCollection<GdxArray<JsonExample>, JsonExample>("ktx/async/assets/collection.json")

      assertEquals(1, storage.getReferencesCount("ktx/async/assets/collection.json"))
      assertSame(asset, storage.loadJsonCollection<GdxArray<JsonExample>, JsonExample>("ktx/async/assets/collection.json"))
      assertEquals(2, storage.getReferencesCount("ktx/async/assets/collection.json"))
      assertSame(asset, storage.loadJson<GdxArray<JsonExample>>("ktx/async/assets/collection.json"))
      assertEquals(3, storage.getReferencesCount("ktx/async/assets/collection.json"))
    }
  }

  @Test
  fun `should increase dependency reference counts recursively`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<Skin>("ktx/async/assets/skin.json")
      storage.load<Skin>("ktx/async/assets/skin.json")
      storage.load<Skin>("ktx/async/assets/skin.json")

      assertTrue(storage.isLoaded("ktx/async/assets/skin.json"))
      assertEquals(3, storage.getReferencesCount("ktx/async/assets/skin.json"))
      assertTrue(storage.isLoaded("ktx/async/assets/skin.atlas"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/skin.atlas"))
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))

      storage.load<TextureAtlas>("ktx/async/assets/skin.atlas")
      assertTrue(storage.isLoaded("ktx/async/assets/skin.atlas"))
      assertEquals(2, storage.getReferencesCount("ktx/async/assets/skin.atlas"))
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))

      storage.dispose()
    }
  }

  @Test
  fun `should recursively unload dependencies of assets scheduled multiple times`()
      = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<Skin>("ktx/async/assets/skin.json")
      storage.load<Skin>("ktx/async/assets/skin.json")

      assertTrue(storage.isLoaded("ktx/async/assets/skin.json"))
      assertEquals(2, storage.getReferencesCount("ktx/async/assets/skin.json"))
      assertTrue(storage.isLoaded("ktx/async/assets/skin.atlas"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/skin.atlas"))
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))

      storage.unload("ktx/async/assets/skin.json")

      assertTrue(storage.isLoaded("ktx/async/assets/skin.json"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/skin.json"))
      assertTrue(storage.isLoaded("ktx/async/assets/skin.atlas"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/skin.atlas"))
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))

      storage.unload("ktx/async/assets/skin.json")

      assertFalse(storage.isLoaded("ktx/async/assets/skin.json"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/skin.json"))
      assertFalse(storage.isLoaded("ktx/async/assets/skin.atlas"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/skin.atlas"))
      assertFalse(storage.isLoaded("ktx/async/assets/texture.png"))
      assertEquals(0, storage.getReferencesCount("ktx/async/assets/texture.png"))
    }
  }

  @Test
  fun `should not throw exception when trying to load the same asset on multiple coroutines at the same time`()
      = `coroutine test`(concurrencyLevel = 4, timeLimitMillis = 60000L) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val expectedTextureReferences = AtomicInteger(0)

    repeat(50) {
      ktxAsync {
        repeat(ThreadLocalRandom.current().nextInt(0, 10)) { skipFrame() }
        expectedTextureReferences.incrementAndGet()
        val asset = storage.load<Texture>("ktx/async/assets/texture.png")

        assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
        assertEquals(expectedTextureReferences.get(), storage.getReferencesCount("ktx/async/assets/texture.png"))
        assertSame(asset, storage.get<Texture>("ktx/async/assets/texture.png"))
        skipFrame()
        assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
        assertEquals(expectedTextureReferences.get(), storage.getReferencesCount("ktx/async/assets/texture.png"))
        expectedTextureReferences.decrementAndGet()
        storage.unload("ktx/async/assets/texture.png")
      }
    }
  }

  @Test
  fun `should not throw exception when trying to load assets with same dependencies on multiple coroutines at the same time`()
      = `coroutine test`(concurrencyLevel = 2, timeLimitMillis = 20000L) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<Skin>("ktx/async/assets/skin.json")

      assertTrue(storage.isLoaded("ktx/async/assets/skin.json"))
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      skipFrame()
      assertTrue(storage.isLoaded("ktx/async/assets/skin.json"))
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      storage.unload("ktx/async/assets/skin.json")
    }

    ktxAsync {
      storage.load<TextureAtlas>("ktx/async/assets/skin.atlas")

      assertTrue(storage.isLoaded("ktx/async/assets/skin.atlas"))
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      storage.unload("ktx/async/assets/skin.atlas")
    }

    ktxAsync {
      storage.load<Skin>("ktx/async/assets/skin.json")

      assertTrue(storage.isLoaded("ktx/async/assets/skin.json"))
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      storage.unload("ktx/async/assets/skin.json")
    }

    ktxAsync {
      storage.load<TextureAtlas>("ktx/async/assets/skin.atlas")

      assertTrue(storage.isLoaded("ktx/async/assets/skin.atlas"))
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      skipFrame()
      assertTrue(storage.isLoaded("ktx/async/assets/skin.atlas"))
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      storage.unload("ktx/async/assets/skin.atlas")
    }

    ktxAsync {
      storage.load<ParticleEffect3d>("ktx/async/assets/particle.p3d")

      assertTrue(storage.isLoaded("ktx/async/assets/particle.p3d"))
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      skipFrame()
      assertTrue(storage.isLoaded("ktx/async/assets/particle.p3d"))
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      storage.unload("ktx/async/assets/particle.p3d")
    }

    ktxAsync {
      storage.load<Texture>("ktx/async/assets/texture.png")

      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      skipFrame()
      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      storage.unload("ktx/async/assets/texture.png")
    }

    ktxAsync {
      storage.load<Texture>("ktx/async/assets/texture.png")

      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      storage.unload("ktx/async/assets/texture.png")
    }
  }

  @Test
  fun `should suspend other coroutines until asset is loaded`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    val assets = Collections.newSetFromMap(IdentityHashMap<Any, Boolean>())
    val loader = mock<SynchronousAssetLoader<Any, AssetLoaderParameters<Any>>> {
      on(it.load(any(), any(), any(), anyOrNull())) doAnswer {
        Thread.sleep(200L)
        Any()
      }
    }
    storage.setLoader(loader)

    repeat(5) {
      ktxAsync {
        val asset = storage.load<Any>("mock")

        assertTrue(storage.isLoaded("mock"))
        assets.add(asset)
        assertEquals(1, assets.size)
        verify(loader, times(1)).load(any(), any(), any(), anyOrNull())
      }
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
  fun `should unload asset`() = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val asset = mock<Disposable>()
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())
    storage.setLoader(mock<SynchronousAssetLoader<Disposable, AssetLoaderParameters<Disposable>>> {
      on(it.load(any(), any(), any(), anyOrNull())) doReturn asset
    })

    ktxAsync {
      storage.load<Disposable>("mock")
      assertTrue(storage.isLoaded("mock"))

      storage.unload("mock")

      assertFalse(storage.isLoaded("mock"))
      assertFalse(storage.assets.containsKey("mock"))
      verify(asset).dispose()
      assertEquals(0, storage.getReferencesCount("mock"))
    }
  }

  @Test
  fun `should not throw an exception when trying to unload absent asset`() {
    val storage = assetStorage()

    storage.unload("test")
  }

  @Test
  fun `should not unload dependency of an unloaded asset if the dependency is referenced by another loaded asset`()
      = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<TextureAtlas>("ktx/async/assets/skin.atlas")
      storage.load<ParticleEffect3d>("ktx/async/assets/particle.p3d")

      storage.unload("ktx/async/assets/skin.atlas")

      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))
      assertFalse(storage.isLoaded("ktx/async/assets/skin.atlas"))
    }
  }

  @Test
  fun `should not unload dependency of an unloaded asset if the dependency was explicitly scheduled for loading`()
      = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<TextureAtlas>("ktx/async/assets/skin.atlas")
      storage.load<Texture>("ktx/async/assets/texture.png")

      storage.unload("ktx/async/assets/skin.atlas")

      assertTrue(storage.isLoaded("ktx/async/assets/texture.png"))
      assertEquals(1, storage.getReferencesCount("ktx/async/assets/texture.png"))
      assertFalse(storage.isLoaded("ktx/async/assets/skin.atlas"))
    }
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
  fun `should dispose of multiple assets of different types without errors`()
      = `coroutine test`(concurrencyLevel = 1) { ktxAsync ->
    val storage = AssetStorage(fileResolver = ClasspathFileHandleResolver())

    ktxAsync {
      storage.load<String>("ktx/async/assets/string.txt")
      storage.load<BitmapFont>("com/badlogic/gdx/utils/arial-15.fnt")
      storage.load<Music>("ktx/async/assets/sound.ogg")
      storage.load<TextureAtlas>("ktx/async/assets/skin.atlas")
      storage.load<Texture>("ktx/async/assets/texture.png")
      storage.load<Skin>("ktx/async/assets/skin.json")
      storage.load<I18NBundle>("ktx/async/assets/i18n")
      storage.load<ParticleEffect>("ktx/async/assets/particle.p2d")
      storage.load<ParticleEffect3d>("ktx/async/assets/particle.p3d")
      storage.load<Model>("ktx/async/assets/model.obj")
      storage.load<Model>("ktx/async/assets/model.g3dj")
      storage.load<Model>("ktx/async/assets/model.g3db")
      storage.load<ShaderProgram>("ktx/async/assets/shader.frag")
      storage.loadJson<JsonExample>("ktx/async/assets/object.json")
      storage.loadJsonCollection<GdxArray<JsonExample>, JsonExample>("ktx/async/assets/collection.json")

      storage.dispose()

      assertEquals(0, storage.assets.size)
      arrayOf(
          "ktx/async/assets/string.txt",
          "com/badlogic/gdx/utils/arial-15.fnt",
          "com/badlogic/gdx/utils/arial-15.png",
          "ktx/async/assets/sound.ogg",
          "ktx/async/assets/skin.atlas",
          "ktx/async/assets/texture.png",
          "ktx/async/assets/skin.json",
          "ktx/async/assets/i18n",
          "ktx/async/assets/particle.p2d",
          "ktx/async/assets/particle.p3d",
          "ktx/async/assets/model.obj",
          "ktx/async/assets/model.g3dj",
          "ktx/async/assets/model.g3db",
          "ktx/async/assets/shader.frag",
          "ktx/async/assets/shader.vert",
          "ktx/async/assets/object.json",
          "ktx/async/assets/collection.json").forEach {
        assertFalse(storage.isLoaded(it))
        assertEquals(0, storage.getReferencesCount(it))
        assertEquals(emptyList<String>(), storage.getDependencies(it))
      }
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

  @Test
  fun `should clear all assets without disposing`() {
    val storage = assetStorage()
    val disposables = (1..5).map { it.toString() to mock<Disposable>() }
        .onEach { (path, asset) -> storage.assets.put(path, asset) }
        .map { (_, asset) -> asset }.toList()

    storage.clear()

    assertEquals(0, storage.assets.size)
    disposables.forEach {
      verify(it, never()).dispose()
    }
  }

  private fun assetStorage() = AssetStorage(
      fileResolver = ClasspathFileHandleResolver(),
      executor = AsyncExecutor(1),
      useDefaultLoaders = false)

  @After
  fun dispose() {
    `destroy coroutines context`()
  }

  /** Used for JSON loading tests. */
  class JsonExample {
    var testInt = 0
    var testString = ""
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
