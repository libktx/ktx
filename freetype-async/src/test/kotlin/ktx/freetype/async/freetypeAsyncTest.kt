package ktx.freetype.async

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver
import com.badlogic.gdx.backends.lwjgl.LwjglFiles
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.nhaarman.mockito_kotlin.mock
import ktx.async.`coroutine test`
import ktx.async.`destroy coroutines context`
import ktx.async.assets.AssetStorage
import ktx.freetype.freeTypeFontParameters
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests FreeType font loading utilities. Uses Hack font for testing. See https://github.com/source-foundry/Hack for
 * font details.
 */
class FreeTypeAsyncTest {
  private val ttfFile = "ktx/freetype/async/hack.ttf"
  private val otfFile = "ktx/freetype/async/hack.otf"

  @Test
  fun `should register FreeType font loaders`() = `coroutine test`(concurrencyLevel = 1) {
    val assetStorage = assetStorage()

    assetStorage.registerFreeTypeFontLoaders()

    assertTrue(assetStorage.getLoader<FreeTypeFontGenerator>() is FreeTypeFontGeneratorLoader)
    assertTrue(assetStorage.getLoader<BitmapFont>(".ttf") is FreetypeFontLoader)
    assertTrue(assetStorage.getLoader<BitmapFont>(".otf") is FreetypeFontLoader)
  }

  @Test
  fun `should register FreeType font loaders with custom extensions`() = `coroutine test`(concurrencyLevel = 1) {
    val assetStorage = assetStorage()

    assetStorage.registerFreeTypeFontLoaders(fileExtensions = arrayOf(".custom"))

    assertTrue(assetStorage.getLoader<FreeTypeFontGenerator>() is FreeTypeFontGeneratorLoader)
    assertTrue(assetStorage.getLoader<BitmapFont>(".custom") is FreetypeFontLoader)
    // Should not register loader for unlisted extensions:
    assertFalse(assetStorage.getLoader<BitmapFont>(".ttf") is FreetypeFontLoader)
    assertFalse(assetStorage.getLoader<BitmapFont>(".otf") is FreetypeFontLoader)
  }

  @Test
  fun `should register FreeType font loaders with default loader override`() = `coroutine test`(concurrencyLevel = 1) {
    val assetStorage = assetStorage()

    assetStorage.registerFreeTypeFontLoaders(replaceDefaultBitmapFontLoader = true)

    assertTrue(assetStorage.getLoader<FreeTypeFontGenerator>() is FreeTypeFontGeneratorLoader)
    assertTrue(assetStorage.getLoader<BitmapFont>() is FreetypeFontLoader)
  }

  @Test
  fun `should load OTF file into BitmapFont`() = `coroutine test`(concurrencyLevel = 1) { async ->
    val assetStorage = assetStorage()
    assetStorage.registerFreeTypeFontLoaders()

    async {
      val asset = assetStorage.loadFreeTypeFont(otfFile)

      val font = assetStorage.get<BitmapFont>(otfFile)
      assertTrue(font is BitmapFont)
      assertSame(asset, font)
    }
  }

  @Test
  fun `should load OTF file into BitmapFont with custom params`() = `coroutine test`(concurrencyLevel = 1) { async ->
    val assetStorage = assetStorage()
    assetStorage.registerFreeTypeFontLoaders()

    async {
      val asset = assetStorage.loadFreeTypeFont(otfFile) {
        size = 12
        borderWidth = 1f
      }

      val font = assetStorage.get<BitmapFont>(otfFile)
      assertTrue(font is BitmapFont)
      assertSame(asset, font)
    }
  }

  @Test
  fun `should load TTF file into BitmapFont`() = `coroutine test`(concurrencyLevel = 1) { async ->
    val assetStorage = assetStorage()
    assetStorage.registerFreeTypeFontLoaders()

    async {
      val asset = assetStorage.loadFreeTypeFont(ttfFile)

      val font = assetStorage.get<BitmapFont>(ttfFile)
      assertTrue(font is BitmapFont)
      assertSame(asset, font)
    }
  }

  @Test
  fun `should load TTF file into BitmapFont with custom params`() = `coroutine test`(concurrencyLevel = 1) { async ->
    val assetStorage = assetStorage()
    assetStorage.registerFreeTypeFontLoaders()

    async {
      val asset = assetStorage.loadFreeTypeFont(ttfFile) {
        size = 12
        borderWidth = 1f
      }

      val font = assetStorage.get<BitmapFont>(ttfFile)
      assertTrue(font is BitmapFont)
      assertSame(asset, font)
    }
  }

  @Test
  fun `should use FreeType loader to load OTF file into BitmapFont`() = `coroutine test`(concurrencyLevel = 1) { async ->
    // Note that this method uses "raw" AssetStorage API without font loading utilities.
    val assetStorage = assetStorage()
    assetStorage.registerFreeTypeFontLoaders()

    async {
      val asset = assetStorage.load<BitmapFont>(otfFile, parameters = freeTypeFontParameters(otfFile))

      val font = assetStorage.get<BitmapFont>(otfFile)
      assertTrue(font is BitmapFont)
      assertSame(font, asset)
    }
  }

  @Test
  fun `should use FreeType loader to load TTF file into BitmapFont`() = `coroutine test`(concurrencyLevel = 1) { async ->
    // Note that this method uses "raw" AssetStorage API without font loading utilities.
    val assetStorage = assetStorage()
    assetStorage.registerFreeTypeFontLoaders()

    async {
      val asset = assetStorage.load<BitmapFont>(ttfFile, parameters = freeTypeFontParameters(ttfFile))

      val font = assetStorage.get<BitmapFont>(ttfFile)
      assertTrue(font is BitmapFont)
      assertSame(font, asset)
    }
  }

  @Test
  fun `should load OTF file into FreeTypeFontGenerator`() = `coroutine test`(concurrencyLevel = 1) { async ->
    val assetStorage = assetStorage()
    assetStorage.registerFreeTypeFontLoaders()

    async {
      val fontGenerator = assetStorage.load<FreeTypeFontGenerator>(otfFile)

      assertNotNull(fontGenerator)
      assertSame(fontGenerator, assetStorage.get<FreeTypeFontGenerator>(otfFile))
    }
  }

  @Test
  fun `should load TTF file into FreeTypeFontGenerator`() = `coroutine test`(concurrencyLevel = 1) { async ->
    val assetStorage = assetStorage()
    assetStorage.registerFreeTypeFontLoaders()

    async {
      val fontGenerator = assetStorage.load<FreeTypeFontGenerator>(ttfFile)

      assertNotNull(fontGenerator)
      assertSame(fontGenerator, assetStorage.get<FreeTypeFontGenerator>(ttfFile))
    }
  }

  @Before
  fun `setup LibGDX`() {
    LwjglNativesLoader.load()
    Gdx.gl = mock()
    Gdx.gl20 = Gdx.gl
    Gdx.graphics = mock()
    Gdx.files = LwjglFiles()
  }

  @After
  fun `cleanup LibGDX`() {
    Gdx.gl = null
    Gdx.gl20 = null
    Gdx.files = null
    Gdx.graphics = null
    `destroy coroutines context`()
  }

  private fun assetStorage() = AssetStorage(fileResolver = ClasspathFileHandleResolver())
}
