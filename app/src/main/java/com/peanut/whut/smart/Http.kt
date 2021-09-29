package com.peanut.whut.smart

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class Http {
    val AGENT =
        "Mozilla/5.0 (Linux; Android 6.0.1; MI 4LTE Build/MMB29M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 Mobile Safari/537.36"
    private var request: Request.Builder? = null
    private var response: Response? = null
    @get:Throws(IOException::class)
    var body: String? = null
        get() {
            if (field == null) {
                field = response!!.body!!.string()
            }
            return field
        }
        private set
    val res:Response?
        get() = response
    private var cur = 0
    private val defaultJump: Request.Builder? = null
    private var isCookieEnabled = true
    private var urlClient: URLClient? = null
    private val client: OkHttpClient.Builder =
        OkHttpClient.Builder().connectTimeout(10L, TimeUnit.SECONDS)

    fun setCookieEnabled(cookieEnabled: Boolean): Http {
        isCookieEnabled = cookieEnabled
        return this
    }

    fun close(){
        response?.body?.close()
    }

    fun setUrlClient(urlClient: URLClient?): Http {
        this.urlClient = urlClient
        return this
    }

    @Throws(Exception::class)
    fun run() {
        response = client.build().newCall(request!!.build()).execute()
        val a = response
        Log.v(
            "response:",
            "" + a!!.code + ",method=" + request!!.build().method
        )
        if (a.code == 302) {
            if (++cur > 10) {
                return
            }
            if (request!!.build().method == "POST") {
                defaultJump!!.url(a.header("Location")!!)
                request = defaultJump
            }
            request!!.url(a.header("Location")!!)
            Log.v("OkHttp", "get=====" + a.header("Location"))
            if (urlClient != null) {
                urlClient!!.shouldOverrideUrlLoading(a.header("Location"))
            }
            this.run()
        }
    }

    fun setHeader(a: String, b: String): Http {
        Log.v("OkHttp", "add=====$a=$b")
        request!!.addHeader(a, b)
        defaultJump?.addHeader(a, b)
        return this
    }

    fun setGet(url: String): Http {
        Log.v("OkHttp", "get=====$url")
        request = Request.Builder().url(url)
        return this
    }

    fun setPost(url: String,body: RequestBody): Http {
        Log.v("OkHttp", "post=====$url")
        request = Request.Builder().url(url).post(body)
        return this
    }

    fun setAutoJump(autoJump: Boolean): Http {
        client.followRedirects(autoJump).followSslRedirects(autoJump)
        return this
    }

    interface URLClient {
        fun shouldOverrideUrlLoading(var1: String?)
    }

}