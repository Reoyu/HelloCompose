package com.example.ch10.data.remote

import com.example.ch10.data.toUserMessage
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

// Retrofit + OkHttp + Kotlinx Serialization SUNGGUHAN, tetapi server-nya
// palsu (MockWebServer) sehingga tidak butuh internet maupun API key.
class NewsApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var api: NewsApiService

    @Before fun setUp() {
        server = MockWebServer().apply { start() }
        // Fungsi yang sama dengan yang dipakai di/NetworkModule, tetapi ke server palsu
        api = createNewsApiService(
            baseUrl = server.url("/").toString(),
            client = createOkHttpClient(apiKey = "kunci-uji", enableLogging = false)
        )
    }

    @After fun tearDown() = server.shutdown()

    @Test fun `JSON NewsAPI terbaca, field asing diabaikan, null diterima`() = runTest {
        server.enqueue(MockResponse().setBody("""
            {
              "status": "ok",
              "totalResults": 1,
              "fieldBaru": true,
              "articles": [{
                "source": {"id": null, "name": "Redaksi"},
                "author": null,
                "title": "Teknologi AI Terbaru",
                "description": "Perkembangan AI",
                "url": "https://example.com/1",
                "urlToImage": "https://example.com/1.jpg",
                "publishedAt": "2025-05-01T08:00:00Z",
                "content": null
              }]
            }
        """.trimIndent()))

        val response = api.getTopHeadlines(category = "technology")

        val dto = response.articles.single()
        assertEquals("Teknologi AI Terbaru", dto.title)
        assertEquals("https://example.com/1.jpg", dto.imageUrl)   // @SerialName("urlToImage")
        assertEquals("Redaksi", dto.source?.name)
        assertNull(dto.author)
    }

    @Test fun `API key dikirim sebagai header, bukan di URL`() = runTest {
        server.enqueue(MockResponse().setBody("""{"status":"ok","totalResults":0,"articles":[]}"""))

        api.getTopHeadlines(category = "sports")

        val request = server.takeRequest()
        assertEquals("kunci-uji", request.getHeader(API_KEY_HEADER))
        assertEquals("/v2/top-headlines", request.requestUrl?.encodedPath)
        assertEquals("sports", request.requestUrl?.queryParameter("category"))
        assertEquals(NEWS_COUNTRY, request.requestUrl?.queryParameter("country"))
        assertNull(request.requestUrl?.queryParameter("apiKey"))
    }

    @Test fun `kata kunci pencarian di-encode dengan benar`() = runTest {
        server.enqueue(MockResponse().setBody("""{"status":"ok","totalResults":0,"articles":[]}"""))

        api.searchNews("harga & kopi")

        val url = server.takeRequest().requestUrl!!
        assertEquals("/v2/everything", url.encodedPath)
        assertEquals("harga & kopi", url.queryParameter("q"))
    }

    @Test fun `401 menjadi HttpException dengan pesan API key`() = runTest {
        server.enqueue(MockResponse().setResponseCode(401).setBody(
            """{"status":"error","code":"apiKeyInvalid","message":"Your API key is invalid"}"""
        ))

        try {
            api.getTopHeadlines()
            fail("seharusnya melempar HttpException")
        } catch (e: HttpException) {
            assertEquals(401, e.code())
            assertEquals(
                "API key tidak valid atau belum diisi. Isi NEWS_API_KEY di local.properties.",
                e.toUserMessage()
            )
        }
    }
}
