package com.example.testgpttovox.response

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URL

data class UnsplashResponse(val results: List<Photo>)
data class Photo(val width: Int, val height: Int, val urls: Urls)
data class Urls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)
/*
 * 参考：
 * https://unsplash.com/documentation
 * full最大寸法の jpg 形式で写真を返します。パフォーマンス上の理由から、写真の読み込みが遅くなるため、これを使用することはお勧めしません。
 * regular幅 1080 ピクセルの jpg 形式で写真を返します。
 * small幅 400 ピクセルの jpg 形式で写真を返します。
 * thumb幅 200 ピクセルの jpg 形式で写真を返します。
 * rawixid写真のパスとAPI アプリケーションのパラメータのみを含む基本画像 URL を返します。これを使用すると、画像パラメータを簡単に追加して独自の画像 URL を作成できます。
 */

@Throws
fun getUnsplashImage(searchTarget: String, url: String, apiKey: String, path: String) {
    val client = OkHttpClient()
    val requestUrl = url.format(searchTarget, apiKey)
    val request = Request.Builder()
        .url(requestUrl)
        .build()
    val response = client.newCall(request).execute()
    val jsonResponse =
        response.body?.string() ?: throw IllegalStateException("No response from API")

    val unsplashResponse = Gson().fromJson(jsonResponse, UnsplashResponse::class.java)

    val images = mutableListOf<File>()

    if(unsplashResponse.results.isEmpty()){
        throw FileNotFoundException("画像を取得できませんでした")
    }

    unsplashResponse.results.filter { photo -> photo.width > photo.height } // 横長の画像のみ
        .take(6) // 最大6枚
        .forEachIndexed { index, photo ->
            val imageUrl = photo.urls.raw + "&w=600"
//        val imageUrl = photo.urls.small
            val file = downloadImage(imageUrl, "$path/photo_$index.jpg")
            images.add(file)
        }
}

fun downloadImage(imageUrl: String, fileName: String): File {
    val url = URL(imageUrl)
    val file = File(fileName)

    url.openStream().use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file
}
