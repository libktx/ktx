package ktx.async

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net.*
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.backends.lwjgl.LwjglNet
import com.badlogic.gdx.net.HttpStatus
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.async.AsyncExecutor
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.nhaarman.mockitokotlin2.*
import io.kotlintest.matchers.shouldThrow
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.alexpanov.net.FreePortFinder
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/**
 * Tests [HttpRequest] API utilities.
 */
class HttpTest {
  @Test
  fun `should cache string representation of content`() {
    // Given:
    val result = httpRequestResponse(content = "test".toByteArray())

    // When:
    val content = result.contentAsString

    // Then:
    assertEquals("test", content)
    val secondCall = result.contentAsString
    assertSame(content, secondCall)
  }

  @Test
  fun `should convert content byte array to stream`() {
    // Given:
    val result = httpRequestResponse(content = byteArrayOf(1, 2, 3))

    // When:
    val stream = result.contentAsStream

    // Then:
    assertArrayEquals(byteArrayOf(1, 2, 3), stream.readBytes())
  }

  @Test
  fun `should not cache content streams`() {
    // Given:
    val result = httpRequestResponse()

    // When:
    val stream = result.contentAsStream
    val secondCall = result.contentAsStream

    // Then:
    assertNotSame(stream, secondCall)
  }

  @Test
  fun `should provide headers`() {
    // Given:
    val result = httpRequestResponse(headers = mapOf("test" to listOf("mock", "testing")))

    // When:
    val header = result.getHeader("test")

    // Then:
    assertEquals(listOf("mock", "testing"), header)
  }

  @Test
  fun `should provide empty headers list if header is missing`() {
    // Given:
    val result = httpRequestResponse(headers = emptyMap())

    // When:
    val header = result.getHeader("test")

    // Then:
    assertTrue(header.isEmpty())
  }

  @Test
  fun `should convert HttpResponse to HttpRequestResult`() {
    // Given:
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

    // When:
    val result = response.toHttpRequestResult(request)

    // Then:
    assertEquals(url, result.url)
    assertEquals(method, result.method)
    assertSame(content, result.content)
    assertEquals(200, result.statusCode)
    assertSame(headers, result.headers)
  }

  @Test
  fun `should safely convert not fully filled HttpResponse to HttpRequestResult`() {
    // Given:
    val url = "https://example.com"
    val method = "GET"
    val request = HttpRequest(method)
    request.url = url
    val response = mock<HttpResponse>()

    // When:
    val result = response.toHttpRequestResult(request)

    // Then:
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

abstract class AsynchronousHttpRequestsTest(private val configuration: LwjglApplicationConfiguration): AsyncTest() {
  private val port = FreePortFinder.findFreeLocalPort()
  @get:Rule
  val wireMock = WireMockRule(port)

  @Test
  fun `should perform asynchronous HTTP request`() {
    // Given:
    Gdx.net = LwjglNet(configuration)
    wireMock.stubFor(get("/test").willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "text/plain")
        .withBody("Test HTTP request.")))

    // When:
    val response = runBlocking {
      httpRequest(
          url = "http://localhost:$port/test",
          method = "GET",
          headers = mapOf("Accept" to "text/plain"))
    }

    // Then:
    assertEquals("http://localhost:$port/test", response.url)
    assertEquals(200, response.statusCode)
    assertEquals("GET", response.method)
    assertEquals("Test HTTP request.", response.contentAsString)
    assertEquals(listOf("text/plain"), response.getHeader("Content-Type"))
  }

  @Test
  fun `should rethrown exceptions of asynchronous HTTP request`() {
    // Given:
    Gdx.net = mock {
      // Always reports the action as failed to the listener:
      on(it.sendHttpRequest(any(), any())) doAnswer { invocation ->
        val listener = invocation.getArgument<HttpResponseListener>(1)
        AsyncExecutor(1).submit {
          listener.failed(GdxRuntimeException("Expected."))
        }
        Unit
      }
    }

    // Expect:
    shouldThrow<GdxRuntimeException> {
      runBlocking { httpRequest(url = "http://example.com", method = "GET") }
    }
  }

  @Test
  fun `should cancel HTTP request`() {
    // Given:
    Gdx.net = spy(LwjglNet(configuration))
    wireMock.stubFor(get("/test").willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "text/plain")
        .withBody("Test HTTP request.")
        .withFixedDelay(1000)))

    // When:
    runBlocking {
      val request = async { httpRequest(url = "http://localhost:$port/test", method = "GET")  }
      launch {
        delay(50L)
        request.cancel()
      }
      request.join()
    }

    // Then:
    verify(Gdx.net).sendHttpRequest(any(), any())
    verify(Gdx.net).cancelHttpRequest(any())
  }
}


/**
 * Tests [httpRequest] API with a single-threaded [LwjglNet].
 */
class SingleThreadAsynchronousHttpRequestsTest : AsynchronousHttpRequestsTest(LwjglApplicationConfiguration().apply {
  maxNetThreads = 1
})

/**
 * Tests [httpRequest] API with a multithreaded [LwjglNet].
 */
class MultiThreadAsynchronousHttpRequestsTest : AsynchronousHttpRequestsTest(LwjglApplicationConfiguration())

class KtxHttpResponseListenerTest {
  @Test
  fun `should invoke cancellation action once`() {
    // Given:
    var executionsAmount = 0
    val listener = KtxHttpResponseListener(mock(), mock(), onCancel = { executionsAmount++ })

    // When:
    listener.cancelled()
    listener.cancelled()
    listener.cancelled()

    // Then:
    assertEquals(1, executionsAmount)
  }

  @Test
  fun `should exceptionally resume coroutine once`() {
    // Given:
    val exception = GdxRuntimeException("Expected.")
    val coroutine = mock<CancellableContinuation<HttpRequestResult>> {
      on(it.isActive) doReturn true
    }
    val listener = KtxHttpResponseListener(mock(), coroutine, mock())

    // When:
    listener.failed(exception)
    listener.failed(exception)
    listener.failed(exception)

    // Then:
    verify(coroutine, times(1)).resumeWith(Result.failure(exception))
  }

  @Test
  fun `should resume coroutine once`() {
    // Given:
    val request = mock<HttpRequest> {
      on(it.url) doReturn "http://example.com"
      on(it.method) doReturn "GET"
    }
    val coroutine = mock<CancellableContinuation<HttpRequestResult>> {
      on(it.isActive) doReturn true
    }
    val response = mock<HttpResponse>()
    val listener = KtxHttpResponseListener(request, coroutine, mock())

    // When:
    listener.handleHttpResponse(response)
    listener.handleHttpResponse(response)
    listener.handleHttpResponse(response)

    // Then:
    verify(coroutine, times(1)).resumeWith(Result.success(response.toHttpRequestResult(request)))
  }


  @Test
  fun `should allow to complete HTTP request once`() {
    // Given:
    val exception = GdxRuntimeException("Expected.")
    val onCancel = mock<(HttpRequest) -> Unit>()
    val coroutine = mock<CancellableContinuation<HttpRequestResult>> {
      on(it.isActive) doReturn true
    }
    val listener = KtxHttpResponseListener(mock(), coroutine, mock())

    // When:
    listener.failed(exception)
    listener.handleHttpResponse(mock())
    listener.cancelled()

    // Then:
    verify(coroutine, times(1)).resumeWith(Result.failure(exception))
    verifyZeroInteractions(onCancel)
  }

  @Test
  fun `should not exceptionally resume inactive coroutine`() {
    // Given:
    val coroutine = mock<CancellableContinuation<HttpRequestResult>>()
    val listener = KtxHttpResponseListener(mock(), coroutine, mock())
    val exception = GdxRuntimeException("Expected.")

    // When:
    listener.failed(exception)

    // Then:
    verify(coroutine, never()).resumeWith(Result.failure(exception))
  }

  @Test
  fun `should not resume inactive coroutine`() {
    // Given:
    val coroutine = mock<CancellableContinuation<HttpRequestResult>>()
    val listener = KtxHttpResponseListener(mock(), coroutine, mock())
    val response = mock<HttpResponse>()
    val request = mock<HttpRequest> {
      on(it.url) doReturn "http://example.com"
      on(it.method) doReturn "GET"
    }

    // When:
    listener.handleHttpResponse(response)

    // Then:
    verify(coroutine, never()).resumeWith(Result.success(response.toHttpRequestResult(request)))
  }
}
