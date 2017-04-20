package ktx.async

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net.*
import com.badlogic.gdx.backends.lwjgl.LwjglNet
import com.badlogic.gdx.net.HttpStatus
import com.badlogic.gdx.utils.GdxRuntimeException
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.nhaarman.mockito_kotlin.*
import io.kotlintest.matchers.shouldThrow
import kotlinx.coroutines.experimental.CancellableContinuation
import me.alexpanov.net.FreePortFinder
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
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

/**
 * Tests [KtxAsync.httpRequest] API.
 */
class AsynchronousHttpRequestsTest {
  val port = FreePortFinder.findFreeLocalPort()
  @get:Rule
  val wireMock = WireMockRule(port)

  @Test
  fun `should perform asynchronous HTTP request`() = `coroutine test`(timeLimitMillis = 10000L) { ktxAsync ->
    Gdx.net = LwjglNet()
    wireMock.stubFor(get("/test").willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "text/plain")
        .withBody("Test HTTP request.")))

    ktxAsync {
      val response = httpRequest(
          url = "http://localhost:$port/test",
          method = "GET",
          headers = mapOf("Accept" to "text/plain"))

      assertEquals("http://localhost:$port/test", response.url)
      assertEquals(200, response.statusCode)
      assertEquals("GET", response.method)
      assertEquals("Test HTTP request.", response.contentAsString)
      assertEquals(listOf("text/plain"), response.getHeader("Content-Type"))
    }
  }

  @Test
  fun `should rethrown exceptions of asynchronous HTTP request`() = `coroutine test` { ktxAsync ->
    Gdx.net = mock {
      // Always reports the action as failed to the listener:
      on(it.sendHttpRequest(any(), any())) doAnswer {
        val listener = it.getArgument<HttpResponseListener>(1)
        scheduler.execute {
          listener.failed(GdxRuntimeException("Expected."))
        }
      }
    }

    ktxAsync {
      shouldThrow<GdxRuntimeException> {
        httpRequest(url = "http://example.com", method = "GET")
      }
    }
  }

  @Test
  fun `should cancel HTTP request`() = `cancelled coroutine test`(testDurationMillis = 250) { ktxAsync, assert ->
    Gdx.net = spy(LwjglNet())
    wireMock.stubFor(get("/test").willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "text/plain")
        .withBody("Test HTTP request.")
        .withFixedDelay(50)))

    ktxAsync {
      httpRequest(url = "http://localhost:$port/test", method = "GET")
      fail("Should be cancelled.")
    }

    assert {
      verify(Gdx.net).cancelHttpRequest(any())
    }
  }

  // HTTP response listener:

  @Test
  fun `should invoke cancellation action once`() {
    var executionsAmount = 0
    val listener = KtxHttpResponseListener(mock(), mock(), onCancel = { executionsAmount++ })

    listener.cancelled()
    listener.cancelled()
    listener.cancelled()

    assertEquals(1, executionsAmount)
  }

  @Test
  fun `should exceptionally resume coroutine once`() {
    val exception = GdxRuntimeException("Expected.")
    val coroutine = mock<CancellableContinuation<HttpRequestResult>> {
      on(it.isActive) doReturn true
    }
    val listener = KtxHttpResponseListener(mock(), coroutine, mock())

    listener.failed(exception)
    listener.failed(exception)
    listener.failed(exception)

    verify(coroutine, times(1)).resumeWithException(exception)
  }

  @Test
  fun `should resume coroutine once`() {
    val request = mock<HttpRequest> {
      on(it.url) doReturn "http://example.com"
      on(it.method) doReturn "GET"
    }
    val coroutine = mock<CancellableContinuation<HttpRequestResult>> {
      on(it.isActive) doReturn true
    }
    val response = mock<HttpResponse>()
    val listener = KtxHttpResponseListener(request, coroutine, mock())

    listener.handleHttpResponse(response)
    listener.handleHttpResponse(response)
    listener.handleHttpResponse(response)

    verify(coroutine, times(1)).resume(any())
  }

  @Test
  fun `should not exceptionally resume inactive coroutine`() {
    val coroutine = mock<CancellableContinuation<HttpRequestResult>>()
    val listener = KtxHttpResponseListener(mock(), coroutine, mock())

    listener.failed(GdxRuntimeException("Expected."))

    verify(coroutine, never()).resumeWithException(any())
  }

  @Test
  fun `should not resume inactive coroutine`() {
    val coroutine = mock<CancellableContinuation<HttpRequestResult>>()
    val listener = KtxHttpResponseListener(mock(), coroutine, mock())

    listener.handleHttpResponse(mock())

    verify(coroutine, never()).resume(any())
  }

  @After
  fun `clear context`() {
    Gdx.net = null
    `destroy coroutines context`()
  }
}
