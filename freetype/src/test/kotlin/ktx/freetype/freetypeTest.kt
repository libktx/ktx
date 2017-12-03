package ktx.freetype

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver
import com.badlogic.gdx.backends.lwjgl.LwjglFiles
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.nhaarman.mockito_kotlin.mock
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests FreeType font loading utilities. Uses Hack font for testing. See https://github.com/source-foundry/Hack for
 * font details.
 */
class FreeTypeTest {
  val ttfFile = "ktx/freetype/hack.ttf"
  val otfFile = "ktx/freetype/hack.otf"

  @Test
  fun `should initiate font loading parameters`() {
    val parameters = freeTypeFontParameters("file.ttf") {
      size = 12
      flip = false
    }

    assertEquals("file.ttf", parameters.fontFileName)
    assertEquals(12, parameters.fontParameters.size)
    assertFalse(parameters.fontParameters.flip)
  }

  @Test
  fun `should register FreeType font loaders`() {
    val assetManager = assetManager()

    assetManager.registerFreeTypeFontLoaders()

    assertTrue(assetManager.getLoader(FreeTypeFontGenerator::class.java) is FreeTypeFontGeneratorLoader)
    assertTrue(assetManager.getLoader(BitmapFont::class.java, ".ttf") is FreetypeFontLoader)
    assertTrue(assetManager.getLoader(BitmapFont::class.java, ".otf") is FreetypeFontLoader)
  }

  @Test
  fun `should register FreeType font loaders with custom extensions`() {
    val assetManager = assetManager()

    assetManager.registerFreeTypeFontLoaders(fileExtensions = arrayOf(".custom"))

    assertTrue(assetManager.getLoader(FreeTypeFontGenerator::class.java) is FreeTypeFontGeneratorLoader)
    assertTrue(assetManager.getLoader(BitmapFont::class.java, ".custom") is FreetypeFontLoader)
    // Should not register loader for unlisted extensions:
    assertFalse(assetManager.getLoader(BitmapFont::class.java, ".ttf") is FreetypeFontLoader)
    assertFalse(assetManager.getLoader(BitmapFont::class.java, ".otf") is FreetypeFontLoader)
  }

  @Test
  fun `should register FreeType font loaders and override default BitmapFont loader`() {
    val assetManager = assetManager()

    assetManager.registerFreeTypeFontLoaders(replaceDefaultBitmapFontLoader = true)

    assertTrue(assetManager.getLoader(FreeTypeFontGenerator::class.java) is FreeTypeFontGeneratorLoader)
    assertTrue(assetManager.getLoader(BitmapFont::class.java) is FreetypeFontLoader)
  }

  @Test
  fun `should load OTF file into BitmapFont`() {
    val assetManager = assetManager()
    assetManager.registerFreeTypeFontLoaders()

    val asset = assetManager.loadFreeTypeFont(otfFile)

    asset.finishLoading()
    val font = assetManager.get<BitmapFont>(otfFile)
    assertNotNull(font)
    assertTrue(font is BitmapFont)
    assertSame(asset.asset, font)
  }

  @Test
  fun `should load OTF file into BitmapFont with custom parameters`() {
    val assetManager = assetManager()
    assetManager.registerFreeTypeFontLoaders()

    val asset = assetManager.loadFreeTypeFont(otfFile) {
      size = 12
      borderWidth = 1f
    }

    asset.finishLoading()
    val font = assetManager.get<BitmapFont>(otfFile)
    assertNotNull(font)
    assertTrue(font is BitmapFont)
    assertSame(asset.asset, font)
  }

  @Test
  fun `should load TTF file into BitmapFont`() {
    val assetManager = assetManager()
    assetManager.registerFreeTypeFontLoaders()

    val asset = assetManager.loadFreeTypeFont(ttfFile)

    asset.finishLoading()
    val font = assetManager.get<BitmapFont>(ttfFile)
    assertNotNull(font)
    assertTrue(font is BitmapFont)
    assertSame(asset.asset, font)
  }

  @Test
  fun `should load TTF file into BitmapFont with custom parameters`() {
    val assetManager = assetManager()
    assetManager.registerFreeTypeFontLoaders()

    val asset = assetManager.loadFreeTypeFont(ttfFile) {
      size = 12
      borderWidth = 1f
    }

    asset.finishLoading()
    val font = assetManager.get<BitmapFont>(ttfFile)
    assertNotNull(font)
    assertTrue(font is BitmapFont)
    assertSame(asset.asset, font)
  }

  @Test
  fun `should use FreeType loader to load OTF file into BitmapFont`() {
    // Note that this tests uses "raw" LibGDX AssetManager API without font loading utilities.
    val assetManager = assetManager()
    assetManager.registerFreeTypeFontLoaders()
    assetManager.load(otfFile, BitmapFont::class.java, freeTypeFontParameters(otfFile))
    assetManager.finishLoading()

    val font = assetManager.get<BitmapFont>(otfFile)

    assertNotNull(font)
    assertTrue(font is BitmapFont)
  }

  @Test
  fun `should use FreeType loader to load TTF file into BitmapFont`() {
    // Note that this tests uses "raw" LibGDX AssetManager API without font loading utilities.
    val assetManager = assetManager()
    assetManager.registerFreeTypeFontLoaders()
    assetManager.load(ttfFile, BitmapFont::class.java, freeTypeFontParameters(ttfFile))
    assetManager.finishLoading()

    val font = assetManager.get<BitmapFont>(ttfFile)

    assertNotNull(font)
    assertTrue(font is BitmapFont)
  }

  @Test
  fun `should load OTF file into FreeTypeFontGenerator`() {
    val assetManager = assetManager()
    assetManager.registerFreeTypeFontLoaders()
    assetManager.load(otfFile, FreeTypeFontGenerator::class.java)
    assetManager.finishLoading()

    val fontGenerator = assetManager.get<FreeTypeFontGenerator>(otfFile)

    assertNotNull(fontGenerator)
    assertTrue(fontGenerator is FreeTypeFontGenerator)
  }

  @Test
  fun `should load TTF file into FreeTypeFontGenerator`() {
    val assetManager = assetManager()
    assetManager.registerFreeTypeFontLoaders()
    assetManager.load(ttfFile, FreeTypeFontGenerator::class.java)
    assetManager.finishLoading()

    val fontGenerator = assetManager.get<FreeTypeFontGenerator>(ttfFile)

    assertNotNull(fontGenerator)
    assertTrue(fontGenerator is FreeTypeFontGenerator)
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
  }

  private fun assetManager() = AssetManager(ClasspathFileHandleResolver())
}
