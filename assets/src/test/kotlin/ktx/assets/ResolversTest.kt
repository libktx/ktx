package ktx.assets

import com.badlogic.gdx.Files.FileType
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver.Resolution
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests [FileHandleResolver] utilities.
 */
class ResolversTest {
  @Test
  fun `should convert Classpath file type to ClasspathFileHandleResolver`() {
    val resolver: FileHandleResolver = FileType.Classpath.getResolver()

    assertTrue(resolver is ClasspathFileHandleResolver)
  }

  @Test
  fun `should convert Internal file type to InternalFileHandleResolver`() {
    val resolver: FileHandleResolver = FileType.Internal.getResolver()

    assertTrue(resolver is InternalFileHandleResolver)
  }

  @Test
  fun `should convert Local file type to LocalFileHandleResolver`() {
    val resolver: FileHandleResolver = FileType.Local.getResolver()

    assertTrue(resolver is LocalFileHandleResolver)
  }

  @Test
  fun `should convert External file type to ExternalFileHandleResolver`() {
    val resolver: FileHandleResolver = FileType.External.getResolver()

    assertTrue(resolver is ExternalFileHandleResolver)
  }

  @Test
  fun `should convert Absolute file type to AbsoluteFileHandleResolver`() {
    val resolver: FileHandleResolver = FileType.Absolute.getResolver()

    assertTrue(resolver is AbsoluteFileHandleResolver)
  }

  @Test
  fun `should decorate FileHandleResolver with PrefixFileHandleResolver`() {
    val resolver = InternalFileHandleResolver()

    val decorated = resolver.withPrefix("test")

    assertSame(resolver, decorated.baseResolver)
    assertEquals("test", decorated.prefix)
  }

  @Test
  fun `should decorate FileHandleResolver with ResolutionFileResolver`() {
    val resolver = InternalFileHandleResolver()

    val decorated = resolver.forResolutions(Resolution(600, 400, "mock"))

    assertSame(resolver, decorated.baseResolver)
    assertEquals(1, decorated.resolutions.size)
    val resolution = decorated.resolutions[0]
    assertEquals(600, resolution.portraitWidth)
    assertEquals(400, resolution.portraitHeight)
    assertEquals("mock", resolution.folder)
  }

  @Test(expected = IllegalArgumentException::class)
  fun `should not decorate FileHandleResolver with ResolutionFileResolver given no resolutions`() {
    val resolver = InternalFileHandleResolver()

    resolver.forResolutions()
  }

  @Test
  fun `should construct Resolution`() {
    val resolution = resolution(width = 800, height = 600, folder = "test")

    assertEquals(800, resolution.portraitWidth)
    assertEquals(600, resolution.portraitHeight)
    assertEquals("test", resolution.folder)
  }

  @Test
  fun `should construct Resolution with default folder name`() {
    val resolution = resolution(width = 1024, height = 768)

    assertEquals(1024, resolution.portraitWidth)
    assertEquals(768, resolution.portraitHeight)
    assertEquals("1024x768", resolution.folder)
  }

  /**
   * Extracts protected [FileHandleResolver] field.
   */
  val ResolutionFileResolver.baseResolver: FileHandleResolver
    get() =
      ResolutionFileResolver::class.java
        .getDeclaredField("baseResolver")
        .apply { isAccessible = true }
        .get(this) as FileHandleResolver

  /**
   * Extracts protected [Resolution] array field.
   */
  @Suppress("UNCHECKED_CAST")
  val ResolutionFileResolver.resolutions: Array<Resolution>
    get() =
      ResolutionFileResolver::class.java
        .getDeclaredField("descriptors")
        .apply { isAccessible = true }
        .get(this) as Array<Resolution>
}
