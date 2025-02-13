package com.example.testgpttovox.service

import com.example.testgpttovox.response.getUnsplashImage
import com.example.testgpttovox.response.translateText
import com.example.testgpttovox.util.*
import com.example.testgpttovox.util.StringUtils.Companion.getSudachiNoun
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GetImageService(
    @Value("\${unsplash.url}") val unsplashUrl: String,
    @Value("\${unsplash.access-key}") val unsplashAccessKey: String,
    @Value("\${google.url}") val googleUrl: String,
    @Value("\${google.api-key}") val googleApiKey: String,
    messageSource: MsgSource,
) : BaseService(messageSource) {

    /**
     * 動画登録
     */
    fun getImage(searchTarget: String, putImagePath: String) {
        logExecution("getImage") {
            // スダチで検索用に名詞のみ取得
            val nouns = getSudachiNoun(searchTarget)

            // google翻訳で日本語から英語に変換
            val translation = if(nouns != null) {
                translateText(nouns.joinToString(separator = ","), googleApiKey, googleUrl)
            } else {
                ""
            }

            // 画像の取得
            getUnsplashImage(translation?:"", unsplashUrl, unsplashAccessKey, putImagePath)
        }
    }

}
