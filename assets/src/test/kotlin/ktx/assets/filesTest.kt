package ktx.assets

import com.badlogic.gdx.Files

import com.badlogic.gdx.Files.FileType
import com.badlogic.gdx.Files.FileType.*
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Tests files-related utilities.
 * @author MJ
 */
class FilesTest {
  @Before
  fun mockFiles() {
    Gdx.files = MockFiles()
  }

  @Test
  fun shouldConvertStringToClasspathFileHandle() {
    val file = "my/package/classpath.file".toClasspathFile()
    assertNotNull(file)
    assertEquals(Classpath, file.type())
    assertEquals("my/package/classpath.file", file.path())
  }

  @Test
  fun shouldConvertStringToInternalFileHandle() {
    val file = "internal.file".toInternalFile()
    assertNotNull(file)
    assertEquals(Internal, file.type())
    assertEquals("internal.file", file.path())
  }

  @Test
  fun shouldConvertStringToLocalFileHandle() {
    val file = "local.file".toLocalFile()
    assertNotNull(file)
    assertEquals(Local, file.type())
    assertEquals("local.file", file.path())
  }

  @Test
  fun shouldConvertStringToExternalFileHandle() {
    val file = "some/directory/external.file".toExternalFile()
    assertNotNull(file)
    assertEquals(External, file.type())
    assertEquals("some/directory/external.file", file.path())
  }

  @Test
  fun shouldConvertStringToAbsoluteFileHandle() {
    val file = "/home/mock/absolute.file".toAbsoluteFile()
    assertNotNull(file)
    assertEquals(Absolute, file.type())
    assertEquals("/home/mock/absolute.file", file.path())
  }
}