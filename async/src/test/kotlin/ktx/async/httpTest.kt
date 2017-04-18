package ktx.async

import com.badlogic.gdx.Net.HttpRequest
import com.badlogic.gdx.Net.HttpResponse
import com.badlogic.gdx.net.HttpStatus
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests [HttpRequest] API utilities.
 */
class HttpTest {
  @Test
  fun `should cache string representation of content`() {
    val result = httpRequestResponse(content = "test".toByteArray())

    val content = result.contentAsString

    assertEquals("test", content)
    val secondCall = result.contentAsString
    assertSame(content, secondCall)
  }

  @Test
  fun `should convert content byte array to stream`() {
    val result = httpRequestResponse(content = byteArrayOf(1, 2, 3))

    val stream = result.contentAsStream

    assertArrayEquals(byteArrayOf(1, 2, 3), stream.readBytes())
  }

  @Test
  fun `should not cache content streams`() {
    val result = httpRequestResponse()

    val stream = result.contentAsStream
    val secondCall = result.contentAsStream

    assertNotSame(stream, secondCall)
  }

  @Test
  fun `should provide headers`() {
    val result = httpRequestResponse(headers = mapOf("test" to listOf("mock", "testing")))

    val header = result.getHeader("test")

    assertEquals(listOf("mock", "testing"), header)
  }

  @Test
  fun `should provide empty headers list if header is missing`() {
    val result = httpRequestResponse(headers = emptyMap())

    val header = result.getHeader("test")

    assertTrue(header.isEmpty())
  }

  @Test
  fun `should convert HttpResponse to HttpRequestResult`() {
    val url = "https://example.com"
    val method = "GET"
    val content = byteArrayOf(1, 2, 3)
    val headers = mapOf("test" to listOf("mock"))
    val status = HttpStatus(200)
    val request = HttpRequest(method)
    request.url = url
    val response = mock<HttpResponse> {
      on(it.result) doReturn content
      on(it.status) doReturn status
      on(it.headers) doReturn headers
    }

    val result = response.toHttpRequestResult(request)

    assertEquals(url, result.url)
    assertEquals(method, result.method)
    assertSame(content, result.content)
    assertEquals(200, result.statusCode)
    assertSame(headers, result.headers)
  }

  @Test
  fun `should safely convert not fully filled HttpResponse to HttpRequestResult`() {
    val url = "https://example.com"
    val method = "GET"
    val request = HttpRequest(method)
    request.url = url
    val response = mock<HttpResponse>()

    val result = response.toHttpRequestResult(request)

    assertEquals(url, result.url)
    assertEquals(method, result.method)
    assertTrue(result.content.isEmpty())
    assertEquals(-1, result.statusCode) // Matches LibGDX unknown status.
    assertTrue(result.headers.isEmpty())
  }

  private fun httpRequestResponse(
      url: String = "https://example.com",
      method: String = "GET",
      content: ByteArray = ByteArray(0),
      status: Int = 200,
      headers: Map<String, List<String>> = emptyMap()
  ) = HttpRequestResult(url, method, status, content, headers)
}
