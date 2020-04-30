package ktx.assets

import com.badlogic.gdx.Files.FileType.Absolute
import com.badlogic.gdx.Files.FileType.Classpath
import com.badlogic.gdx.Files.FileType.External
import com.badlogic.gdx.Files.FileType.Internal
import com.badlogic.gdx.Files.FileType.Local
import com.badlogic.gdx.Gdx
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Tests files-related utilities.
 */
class FilesTest {
  @Before
  fun `mock Files`() {
    Gdx.files = MockFiles()
  }

  @Test
  fun `should convert string to classpath FileHandle`() {
    val file = "my/package/classpath.file".toClasspathFile()

    assertNotNull(file)
    assertEquals(Classpath, file.type())
    assertEquals("my/package/classpath.file", file.path())
  }

  @Test
  fun `should convert string to internal FileHandle`() {
    val file = "internal.file".toInternalFile()

    assertNotNull(file)
    assertEquals(Internal, file.type())
    assertEquals("internal.file", file.path())
  }

  @Test
  fun `should convert string to local FileHandle`() {
    val file = "local.file".toLocalFile()

    assertNotNull(file)
    assertEquals(Local, file.type())
    assertEquals("local.file", file.path())
  }

  @Test
  fun `should convert string to external FileHandle`() {
    val file = "some/directory/external.file".toExternalFile()

    assertNotNull(file)
    assertEquals(External, file.type())
    assertEquals("some/directory/external.file", file.path())
  }

  @Test
  fun `should convert string to absolute FileHandle`() {
    val file = "/home/mock/absolute.file".toAbsoluteFile()

    assertNotNull(file)
    assertEquals(Absolute, file.type())
    assertEquals("/home/mock/absolute.file", file.path())
  }

  @Test
  fun `should create FileHandle with default type`() {
    val file = file("mock.file")

    assertNotNull(file)
    assertEquals(Internal, file.type())
    assertEquals("mock.file", file.path())
  }

  @Test
  fun `should create FileHandle with custom type`() {
    val file = file("/home/ktx/mock.file", type = Absolute)

    assertNotNull(file)
    assertEquals(Absolute, file.type())
    assertEquals("/home/ktx/mock.file", file.path())
  }
}
