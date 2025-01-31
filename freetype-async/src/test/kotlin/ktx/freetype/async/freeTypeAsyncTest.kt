package ktx.freetype.async

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import io.kotlintest.matchers.shouldThrow
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ktx.assets.async.AssetStorage
import ktx.assets.async.AsyncAssetManager
import ktx.assets.async.Identifier
import ktx.assets.async.MissingAssetException
import ktx.async.AsyncTest
import ktx.async.newAsyncContext
import ktx.freetype.FreeTypeTest
import ktx.freetype.freeTypeFontParameters
import ktx.freetype.registerFreeTypeFontLoaders
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.kotlin.mock

/**
 * Tests FreeType font loading utilities for [AssetStorage].
 *
 * Uses Hack font for testing. See https://github.com/source-foundry/Hack for font details.
 *
 * Implementation note: tests use [runBlocking] to simplify the implementation. This is
 * a blocking operation and might permanently block your rendering thread. In an actual
 * application, prefer [ktx.async.KtxAsync].launch to run your coroutines.
 */
class AssetStorageFreeTypeTest : AsyncTest() {
  private val ttfFile = "ktx/freetype/async/hack.ttf"
  private val otfFile = "ktx/freetype/async/hack.otf"

  companion object {
    @JvmStatic
    @BeforeClass
    fun `initiate libGDX`() {
      LwjglNativesLoader.load()
      Gdx.graphics = mock()
      Gdx.gl20 = mock()
      Gdx.gl = Gdx.gl20
    }
  }

  @Test
  fun `should register FreeType font loaders`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)

    // When:
    storage.registerFreeTypeFontLoaders()

    // Then:
    assertTrue(storage.getLoader<FreeTypeFontGenerator>() is FreeTypeFontGeneratorLoader)
    assertTrue(storage.getLoader<BitmapFont>(".ttf") is FreetypeFontLoader)
    assertTrue(storage.getLoader<BitmapFont>(".otf") is FreetypeFontLoader)
    // Default font loader should not be registered:
    assertNull(storage.getLoader<BitmapFont>())
  }

  @Test
  fun `should register FreeType font loaders with custom extensions`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false)

    // When:
    storage.registerFreeTypeFontLoaders(fileExtensions = arrayOf(".custom"))

    // Then:
    assertTrue(storage.getLoader<FreeTypeFontGenerator>() is FreeTypeFontGeneratorLoader)
    assertTrue(storage.getLoader<BitmapFont>(".custom") is FreetypeFontLoader)
    // Should not register loader for unlisted extensions:
    assertNull(storage.getLoader<BitmapFont>(".ttf"))
    assertNull(storage.getLoader<BitmapFont>(".otf"))
    assertNull(storage.getLoader<BitmapFont>())
  }

  @Test
  fun `should register FreeType font loaders with default font loader override`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = true)

    // When:
    storage.registerFreeTypeFontLoaders(replaceDefaultBitmapFontLoader = true)

    // Then:
    assertTrue(storage.getLoader<FreeTypeFontGenerator>() is FreeTypeFontGeneratorLoader)
    assertTrue(storage.getLoader<BitmapFont>() is FreetypeFontLoader)
  }

  @Test
  fun `should load OTF file into BitmapFont`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset = runBlocking { storage.loadFreeTypeFont(otfFile) }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(otfFile))
    assertSame(asset, storage.get<BitmapFont>(otfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(otfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$otfFile.gen")),
      storage.getDependencies<BitmapFont>(otfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$otfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should load OTF file into BitmapFont asynchronously`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset = runBlocking { storage.loadFreeTypeFontAsync(otfFile).await() }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(otfFile))
    assertSame(asset, storage.get<BitmapFont>(otfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(otfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$otfFile.gen")),
      storage.getDependencies<BitmapFont>(otfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$otfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should load OTF file into BitmapFont synchronously`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset = storage.loadFreeTypeFontSync(otfFile)

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(otfFile))
    assertSame(asset, storage.get<BitmapFont>(otfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(otfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$otfFile.gen")),
      storage.getDependencies<BitmapFont>(otfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$otfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should load OTF file into BitmapFont with custom params`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset =
      runBlocking {
        storage.loadFreeTypeFont(otfFile) {
          size = 12
          borderWidth = 1f
        }
      }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(otfFile))
    assertSame(asset, storage.get<BitmapFont>(otfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(otfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$otfFile.gen")),
      storage.getDependencies<BitmapFont>(otfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$otfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should load OTF file into BitmapFont with custom params asynchronously`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset =
      runBlocking {
        storage
          .loadFreeTypeFontAsync(otfFile) {
            size = 12
            borderWidth = 1f
          }.await()
      }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(otfFile))
    assertSame(asset, storage.get<BitmapFont>(otfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(otfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$otfFile.gen")),
      storage.getDependencies<BitmapFont>(otfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$otfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should load OTF file into BitmapFont with custom params synchronously`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset =
      storage.loadFreeTypeFontSync(otfFile) {
        size = 12
        borderWidth = 1f
      }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(otfFile))
    assertSame(asset, storage.get<BitmapFont>(otfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(otfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$otfFile.gen")),
      storage.getDependencies<BitmapFont>(otfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$otfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should unload BitmapFont assets loaded from OTF file`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()
    storage.loadFreeTypeFontSync(otfFile)

    // When:
    runBlocking { storage.unload<BitmapFont>(otfFile) }

    // Then:
    assertFalse(storage.isLoaded<BitmapFont>(otfFile))
    assertEquals(0, storage.getReferenceCount<BitmapFont>(otfFile))
    assertFalse(storage.isLoaded<FreeTypeFontGenerator>("$otfFile.gen"))
    assertEquals(0, storage.getReferenceCount<FreeTypeFontGenerator>("$otfFile.gen"))
  }

  @Test
  fun `should load TTF file into BitmapFont`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset = runBlocking { storage.loadFreeTypeFont(ttfFile) }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(ttfFile))
    assertSame(asset, storage.get<BitmapFont>(ttfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(ttfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$ttfFile.gen")),
      storage.getDependencies<BitmapFont>(ttfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$ttfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should load TTF file into BitmapFont asynchronously`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset = runBlocking { storage.loadFreeTypeFontAsync(ttfFile).await() }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(ttfFile))
    assertSame(asset, storage.get<BitmapFont>(ttfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(ttfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$ttfFile.gen")),
      storage.getDependencies<BitmapFont>(ttfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$ttfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should load TTF file into BitmapFont synchronously`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset = storage.loadFreeTypeFontSync(ttfFile)

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(ttfFile))
    assertSame(asset, storage.get<BitmapFont>(ttfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(ttfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$ttfFile.gen")),
      storage.getDependencies<BitmapFont>(ttfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$ttfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should load TTF file into BitmapFont with custom params`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset =
      runBlocking {
        storage.loadFreeTypeFont(ttfFile) {
          size = 12
          borderWidth = 1f
        }
      }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(ttfFile))
    assertSame(asset, storage.get<BitmapFont>(ttfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(ttfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$ttfFile.gen")),
      storage.getDependencies<BitmapFont>(ttfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$ttfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should load TTF file into BitmapFont with custom params asynchronously`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset =
      runBlocking {
        storage
          .loadFreeTypeFontAsync(ttfFile) {
            size = 12
            borderWidth = 1f
          }.await()
      }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(ttfFile))
    assertSame(asset, storage.get<BitmapFont>(ttfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(ttfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$ttfFile.gen")),
      storage.getDependencies<BitmapFont>(ttfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$ttfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should load TTF file into BitmapFont with custom params synchronously`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset =
      storage.loadFreeTypeFontSync(ttfFile) {
        size = 12
        borderWidth = 1f
      }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(ttfFile))
    assertSame(asset, storage.get<BitmapFont>(ttfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(ttfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$ttfFile.gen")),
      storage.getDependencies<BitmapFont>(ttfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$ttfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should unload BitmapFont assets loaded from TTF file`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()
    storage.loadFreeTypeFontSync(ttfFile)

    // When:
    runBlocking { storage.unload<BitmapFont>(ttfFile) }

    // Then:
    assertFalse(storage.isLoaded<BitmapFont>(ttfFile))
    assertEquals(0, storage.getReferenceCount<BitmapFont>(ttfFile))
    assertFalse(storage.isLoaded<FreeTypeFontGenerator>("$ttfFile.gen"))
    assertEquals(0, storage.getReferenceCount<FreeTypeFontGenerator>("$ttfFile.gen"))
  }

  @Test
  fun `should use FreeType loader to load OTF file into BitmapFont`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset =
      runBlocking {
        // Note that this method uses "raw" AssetStorage API without font loading utilities.
        // Without the freeTypeFontParameters, this will fail to load.
        storage.load<BitmapFont>(otfFile, parameters = freeTypeFontParameters(otfFile))
      }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(otfFile))
    assertSame(asset, storage.get<BitmapFont>(otfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(otfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$otfFile.gen")),
      storage.getDependencies<BitmapFont>(otfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$otfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should use FreeType loader to load TTF file into BitmapFont`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset =
      runBlocking {
        // Note that this method uses "raw" AssetStorage API without font loading utilities.
        // Without the freeTypeFontParameters, this will fail to load.
        storage.load<BitmapFont>(ttfFile, parameters = freeTypeFontParameters(ttfFile))
      }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(ttfFile))
    assertSame(asset, storage.get<BitmapFont>(ttfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(ttfFile))
    // Automatically loads a generator for the font:
    assertEquals(
      listOf(storage.getIdentifier<FreeTypeFontGenerator>("$ttfFile.gen")),
      storage.getDependencies<BitmapFont>(ttfFile),
    )
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$ttfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should load OTF file into FreeTypeFontGenerator`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset = runBlocking { storage.load<FreeTypeFontGenerator>(otfFile) }

    // Then:
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>(otfFile))
    assertSame(asset, storage.get<FreeTypeFontGenerator>(otfFile))
    assertEquals(1, storage.getReferenceCount<FreeTypeFontGenerator>(otfFile))
    assertEquals(emptyList<Identifier<*>>(), storage.getDependencies<FreeTypeFontGenerator>(otfFile))

    storage.dispose()
  }

  @Test
  fun `should load TTF file into FreeTypeFontGenerator`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()

    // When:
    val asset = runBlocking { storage.load<FreeTypeFontGenerator>(ttfFile) }

    // Then:
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>(ttfFile))
    assertSame(asset, storage.get<FreeTypeFontGenerator>(ttfFile))
    assertEquals(1, storage.getReferenceCount<FreeTypeFontGenerator>(ttfFile))
    assertEquals(emptyList<Identifier<*>>(), storage.getDependencies<FreeTypeFontGenerator>(ttfFile))

    storage.dispose()
  }

  @Test
  fun `should configure font parameters exactly once`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()
    runBlocking {
      val variable: Int

      // When:
      storage.loadFreeTypeFont(ttfFile) {
        variable = 42
      }

      // Then:
      assertEquals(42, variable)
    }
  }

  @Test
  fun `should allow to load BitmapFont and FreeTypeFontGenerator assets in parallel`() {
    // Given:
    val storage =
      AssetStorage(
        useDefaultLoaders = false,
        fileResolver = ClasspathFileHandleResolver(),
        asyncContext = newAsyncContext(threads = 4),
      )
    storage.registerFreeTypeFontLoaders()

    // When:
    runBlocking {
      val otf = async { storage.loadFreeTypeFont(otfFile) }
      val ttf = async { storage.loadFreeTypeFont(ttfFile) }
      val otfGenerator = async { storage.load<FreeTypeFontGenerator>(otfFile) }
      val ttfGenerator = async { storage.load<FreeTypeFontGenerator>(ttfFile) }

      otf.await()
      ttf.await()
      otfGenerator.await()
      ttfGenerator.await()
    }

    // Then:
    assertTrue(storage.isLoaded<BitmapFont>(otfFile))
    assertTrue(storage.isLoaded<BitmapFont>(ttfFile))
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>(otfFile))
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>(ttfFile))
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$otfFile.gen"))
    assertTrue(storage.isLoaded<FreeTypeFontGenerator>("$ttfFile.gen"))

    assertEquals(1, storage.getReferenceCount<BitmapFont>(otfFile))
    assertEquals(1, storage.getReferenceCount<BitmapFont>(ttfFile))
    assertEquals(1, storage.getReferenceCount<FreeTypeFontGenerator>(otfFile))
    assertEquals(1, storage.getReferenceCount<FreeTypeFontGenerator>(ttfFile))
    assertEquals(1, storage.getReferenceCount<FreeTypeFontGenerator>("$otfFile.gen"))
    assertEquals(1, storage.getReferenceCount<FreeTypeFontGenerator>("$ttfFile.gen"))

    storage.dispose()
  }

  @Test
  fun `should dispose of multiple font assets without errors`() {
    // Given:
    val storage = AssetStorage(useDefaultLoaders = false, fileResolver = ClasspathFileHandleResolver())
    storage.registerFreeTypeFontLoaders()
    val assets =
      listOf(
        storage.getAssetDescriptor<BitmapFont>(otfFile, parameters = freeTypeFontParameters(otfFile)),
        storage.getAssetDescriptor<BitmapFont>(ttfFile, parameters = freeTypeFontParameters(otfFile)),
        storage.getAssetDescriptor<FreeTypeFontGenerator>(otfFile),
        storage.getAssetDescriptor<FreeTypeFontGenerator>(ttfFile),
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
      assertEquals(emptyList<Identifier<*>>(), storage.getDependencies(it))
      shouldThrow<MissingAssetException> {
        storage[it]
      }
    }
  }
}

/**
 * Tests FreeType font loading utilities for [AsyncAssetManager].
 *
 * Uses Hack font for testing. See https://github.com/source-foundry/Hack for font details.
 *
 * Implementation note: tests use [runBlocking] to simplify the implementation. This is
 * a blocking operation and might permanently block your rendering thread. In an actual
 * application, prefer [ktx.async.KtxAsync].launch to run your coroutines and obtain
 * [kotlinx.coroutines.Deferred] instances via await.
 */
class AsyncAssetManagerFreeTypeTest : FreeTypeTest() {
  override val ttfFile = "ktx/freetype/async/hack.ttf"
  override val otfFile = "ktx/freetype/async/hack.otf"

  override fun assetManager(): AsyncAssetManager = AsyncAssetManager(ClasspathFileHandleResolver())

  @Test
  fun `should load OTF file into BitmapFont asynchronously`() {
    // Given:
    val assetManager = assetManager()
    assetManager.registerFreeTypeFontLoaders()

    // When:
    val reference = assetManager.loadFreeTypeFontAsync(otfFile)

    // Then:
    assetManager.finishLoading()
    val asset = runBlocking { reference.await() }
    val font = assetManager.get<BitmapFont>(otfFile)
    assertNotNull(font)
    assertTrue(font is BitmapFont)
    assertSame(asset, font)
  }

  @Test
  fun `should load OTF file into BitmapFont with custom parameters asynchronously`() {
    // Given:
    val assetManager = assetManager()
    assetManager.registerFreeTypeFontLoaders()
    val variable: Int

    // When:
    val reference =
      assetManager.loadFreeTypeFontAsync(otfFile) {
        size = 12
        borderWidth = 1f
        variable = 42
      }

    // Then:
    assetManager.finishLoading()
    val asset = runBlocking { reference.await() }
    val font = assetManager.get<BitmapFont>(otfFile)
    assertNotNull(font)
    assertTrue(font is BitmapFont)
    assertSame(asset, font)
    assertEquals(42, variable)
  }

  @Test
  fun `should load TTF file into BitmapFont asynchronously`() {
    // Given:
    val assetManager = assetManager()
    assetManager.registerFreeTypeFontLoaders()

    // When:
    val reference = assetManager.loadFreeTypeFontAsync(ttfFile)

    // Then:
    assetManager.finishLoading()
    val asset = runBlocking { reference.await() }
    val font = assetManager.get<BitmapFont>(ttfFile)
    assertNotNull(font)
    assertTrue(font is BitmapFont)
    assertSame(asset, font)
  }

  @Test
  fun `should load TTF file into BitmapFont with custom parameters asynchronously`() {
    // Given:
    val assetManager = assetManager()
    assetManager.registerFreeTypeFontLoaders()
    val variable: Int

    // When:
    val reference =
      assetManager.loadFreeTypeFontAsync(ttfFile) {
        size = 12
        borderWidth = 1f
        variable = 42
      }

    // Then:
    assetManager.finishLoading()
    val asset = runBlocking { reference.await() }
    val font = assetManager.get<BitmapFont>(ttfFile)
    assertNotNull(font)
    assertTrue(font is BitmapFont)
    assertSame(asset, font)
    assertEquals(42, variable)
  }
}
