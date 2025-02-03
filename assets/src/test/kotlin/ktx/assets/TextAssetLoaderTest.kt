package ktx.assets

import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException
import io.kotlintest.matchers.shouldThrow
import ktx.assets.TextAssetLoader.TextAssetLoaderParameters
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/**
 * Tests [TextAssetLoader]: [AssetLoader] implementation for asynchronous reading of text files.
 */
class TextAssetLoaderTest {
  @Test
  fun `should read text data from FileHandle`() {
    // Given:
    val loader = TextAssetLoader(charset = "UTF-8")
    val file =
      mock<FileHandle> {
        on(it.readString("UTF-8")) doReturn "Content."
      }

    // When:
    loader.loadAsync(mock(), "test.txt", file, null)
    val result = loader.loadSync(mock(), "test.txt", file, null)

    // Then:
    Assert.assertEquals("Content.", result)
    verify(file).readString("UTF-8")
  }

  @Test
  fun `should read text data from FileHandle with loading parameters`() {
    // Given:
    val loader = TextAssetLoader(charset = "UTF-8")
    val file =
      mock<FileHandle> {
        on(it.readString("UTF-16")) doReturn "Content."
      }

    // When:
    loader.loadAsync(mock(), "test.txt", file, TextAssetLoaderParameters(charset = "UTF-16"))
    val result = loader.loadSync(mock(), "test.txt", file, TextAssetLoaderParameters(charset = "UTF-16"))

    // Then:
    Assert.assertEquals("Content.", result)
    verify(file).readString("UTF-16")
  }

  @Test
  fun `should throw exception when trying to read file without loading it asynchronously`() {
    // Given:
    val loader = TextAssetLoader(charset = "UTF-8")

    // Expect:
    shouldThrow<GdxRuntimeException> {
      loader.loadSync(mock(), "test.txt", mock(), null)
    }
  }
}
