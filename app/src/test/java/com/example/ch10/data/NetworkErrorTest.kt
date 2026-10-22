package com.example.ch10.data

import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class NetworkErrorTest {

    private fun http(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))

    @Test fun `tanpa internet`() =
        assertEquals("Tidak ada koneksi internet.", UnknownHostException("newsapi.org").toUserMessage())

    @Test fun `timeout dibedakan dari tanpa internet`() =
        assertEquals("Koneksi terlalu lambat. Coba lagi.", SocketTimeoutException().toUserMessage())

    @Test fun `IOException lain dianggap masalah koneksi`() =
        assertEquals("Tidak ada koneksi internet.", IOException().toUserMessage())

    @Test fun `kode HTTP`() {
        assertEquals("Batas permintaan NewsAPI tercapai. Coba lagi nanti.", http(429).toUserMessage())
        assertEquals("Server sedang bermasalah (kode 503). Coba lagi nanti.", http(503).toUserMessage())
        assertEquals("Permintaan gagal (kode 400).", http(400).toUserMessage())
    }

    @Test fun `JSON tidak sesuai DTO`() =
        assertEquals("Format data dari server tidak dikenali.", SerializationException("x").toUserMessage())
}
