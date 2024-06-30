package dev.poropi.gpt4oscouter.service.web.interceptor

import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.internal.platform.Platform
import okio.Buffer
import timber.log.Timber
import java.io.IOException

/**
 * An OkHttp interceptor which logs request and response information in cURL format.
 */
class CurlHttpLoggingInterceptor @JvmOverloads constructor(private val logger: Logger = Logger.DEFAULT) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val method = request.method
        var bodyString = ""
        var contentType = ""
        if (request.body != null) {
            val body = parseRequestBody(request.body)
            if (body != null && body != "") {
                bodyString = "-d '$body'"
            }
            val type = request.body!!.contentType()
            if (type != null) {
                contentType = "-H Content-Type: '$type'"
            }
        }
        val headers = request.headers
        val headersBuilder = StringBuilder()
        var i = 0
        val size = headers.size
        while (i < size) {
            headersBuilder.append(" -H '" + headers.name(i)).append(": ").append("""
    ${headers.value(i)}' \
    
    """.trimIndent())
            ++i
        }
        val headersString = headersBuilder.toString()
        Timber.d(""" \
curl  -X $method \
 ${if (contentType != "") "$contentType \\\n" else ""}$headersString${if (bodyString != "") "$bodyString \\\n" else ""}'$url'""")
        return chain.proceed(request)
    }

    private fun parseRequestBody(body: RequestBody?): String? {
        return if (body == null) {
            null
        } else {
            try {
                val buffer = Buffer()
                body.writeTo(buffer)
                buffer.readUtf8()
            } catch (var3: IOException) {
                null
            }
        }
    }

    interface Logger {
        fun log(var1: String?)

        companion object {
            val DEFAULT: Logger = object : Logger {
                override fun log(var1: String?) {
                    var1?:return
                    Platform.get().log(var1, 4, null as Throwable?)
                }
            }
        }
    }
}
