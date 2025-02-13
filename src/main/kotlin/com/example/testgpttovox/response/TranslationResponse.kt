package com.example.testgpttovox.response

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

data class TranslationResponse(val data: Data)
data class Data(val translations: List<Translation>)
data class Translation(val translatedText: String)

fun translateText(text: String, apiKey: String, url: String): String? {
    val client = OkHttpClient()

    // APIリクエストのボディ
    val json = """
        {
            "q": "$text",
            "source": "ja",
            "target": "en",
            "format": "text"
        }
    """

    // APIリクエストの構築
    val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    val request = Request.Builder()
        .url("$url?key=$apiKey")
        .post(body)
        .build()

    // リクエストを実行し、結果を取得
    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        val responseBody = response.body?.string() ?: return null
        val gson = Gson()

        // Google APIのレスポンスをパース
        val translationResponse = gson.fromJson(responseBody, TranslationResponse::class.java)
        return translationResponse.data.translations[0].translatedText
    }
}