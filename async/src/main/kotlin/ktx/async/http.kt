package ktx.async

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Net.*
import kotlinx.coroutines.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.Arrays
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Executes a HTTP request asynchronously.
 * @param url URL that will be queried.
 * @param method HTTP method. GET by default. See [com.badlogic.gdx.Net.HttpMethods].
 * @param headers HTTP request headers mapped to their values. Empty by default.
 * @param timeout time before the request is cancelled. If the request was cancelled, an exception will be thrown
 *    (type of exception might vary on each platform).
 * @param content body of the request.
 * @param contentStream body of the request. Alternative to [content]. Pair of an [InputStream] along with its size.
 * @param followRedirects whether 301 and 302 redirects are followed. Defaults to true.
 * @param includeCredentials whether a cross-origin request will include credentials. Relevant only on web platforms.
 * @param onCancel executed when the HTTP request is cancelled through coroutine cancellation. Optional, should be
 *    passed only if coroutine's [Job.cancel] can be called.
 * @return [HttpRequestResult] storing HTTP response data.
 * @see HttpRequest
 */
suspend fun httpRequest(
    url: String,
    method: String = "GET",
    headers: Map<String, String> = emptyMap(),
    timeout: Int = 0,
    content: String? = null,
    contentStream: Pair<InputStream, Long>? = null,
    followRedirects: Boolean = true,
    includeCredentials: Boolean = false,
    onCancel: ((HttpRequest) -> Unit)? = null
): HttpRequestResult = coroutineScope {
  suspendCancellableCoroutine<HttpRequestResult> { continuation ->
    val httpRequest = HttpRequest(method).apply {
      this.url = url
      timeOut = timeout
      this.content = content
      this.followRedirects = followRedirects
      this.includeCredentials = includeCredentials
      contentStream?.let { setContent(it.first, it.second) }
      headers.forEach { (header, value) -> setHeader(header, value) }
    }
    val listener = KtxHttpResponseListener(httpRequest, continuation, onCancel)
    Gdx.net.sendHttpRequest(httpRequest, listener)
    continuation.invokeOnCancellation {
      if (!listener.completed) {
        Gdx.net.cancelHttpRequest(httpRequest)
      }
    }
  }
}

/**
 * Stores result of a [HttpRequest]. A safer alternative to [HttpResponse].
 * @param url URL of the queried resource.
 * @param method HTTP method of the request.
 * @param statusCode HTTP status code of the response. Might be set to -1 by internal LibGDX implementation or if the
 *    status could not be determined.
 * @param content response body stored as raw bytes.
 * @param headers HTTP header values of the response. Might be empty.
 */
class HttpRequestResult(
    val url: String,
    val method: String,
    val statusCode: Int,
    val content: ByteArray,
    val headers: Map<String, List<String>>
) {
  /** Returns cached representation of the response stored as a string with default encoding.*/
  val contentAsString by lazy { getContentAsString() }
  /** Returns a new instance of [ByteArrayInputStream] with raw response bytes each time the getter is invoked. */
  val contentAsStream get() = ByteArrayInputStream(content)

  /**
   * @param header name of the HTTP header.
   * @return values assigned to the header or empty list if header is not present.
   */
  fun getHeader(header: String) = headers[header] ?: emptyList()

  /**
   * @param charset character encoding. Defaults to UTF-8.
   * @return [content] converted to a string using the selected encoding.
   */
  fun getContentAsString(charset: Charset = Charsets.UTF_8) = String(content, charset)

  override fun toString() = "HttpRequestResult(url=$url, method=$method, status=$statusCode)"
  override fun equals(other: Any?) = when (other) {
    null -> false
    other === this -> true
    is HttpRequestResult -> url == other.url && method == other.method && statusCode == other.statusCode
        && Arrays.equals(content, other.content) && headers == other.headers
    else -> false
  }

  override fun hashCode(): Int {
    var result = url.hashCode()
    result = 31 * result + method.hashCode()
    result = 31 * result + statusCode
    result = 31 * result + content.contentHashCode()
    result = 31 * result + headers.hashCode()
    return result
  }

  // Implementation note: LibGDX HttpRequestResult implementation can _quietly_ ignore closed input streams, which might
  // result in empty responses in multithreaded environments. We read the whole response into a byte array and return
  // this data object to avoid response content loss.
}

/**
 * Converts this non thread-safe [HttpResponse] to [HttpRequestResult] that reads and caches the HTTP result content
 * as byte array. Note that this method blocks the current thread until the HTTP result content is read.
 * @param requestData necessary to extract relevant data about the original request.
 * @return a new [HttpRequestResult] storing [HttpResponse] content.
 */
fun HttpResponse.toHttpRequestResult(requestData: HttpRequest) = HttpRequestResult(
    url = requestData.url,
    method = requestData.method,
    statusCode = this.status?.statusCode ?: -1, // -1 matches LibGDX default behaviour on unknown status.
    content = this.result ?: ByteArray(0),
    headers = this.headers ?: emptyMap()
)

/**
 * Internal implementation of [HttpResponseListener] based on coroutines.
 * @param httpRequest listens to the HTTP response of this request.
 * @param continuation will be resumed once the response is received.
 * @param onCancel optional operation executed if the coroutine is cancelled during the HTTP request.
 */
internal class KtxHttpResponseListener(
    val httpRequest: HttpRequest,
    val continuation: CancellableContinuation<HttpRequestResult>,
    val onCancel: ((HttpRequest) -> Unit)?
) : HttpResponseListener {
  @Volatile
  var completed = false
    private set

  override fun cancelled() = complete {
    onCancel?.invoke(httpRequest)
  }

  override fun failed(exception: Throwable) = complete {
    if (continuation.isActive) {
      continuation.resumeWithException(exception)
    }
  }

  override fun handleHttpResponse(httpResponse: HttpResponse) = complete {
    if (continuation.isActive) {
      continuation.resume(httpResponse.toHttpRequestResult(httpRequest))
    }
  }

  private inline fun complete(action: () -> Unit) {
    if (!completed) {
      completed = true
      action()
    }
  }
}
