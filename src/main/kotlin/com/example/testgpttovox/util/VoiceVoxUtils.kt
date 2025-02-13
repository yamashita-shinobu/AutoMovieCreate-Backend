package com.example.testgpttovox.util

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

@Component
class VoiceVoxUtils(private val propertyProvider: PropertyProvider) {
    companion object {
        private val client = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        private lateinit var voiceVoxUrl: String
        private lateinit var nemoUrl: String
        private lateinit var voiceVoxSynthesisUrl: String
        private lateinit var nemoSynthesisUrl: String

        fun init(propertyProvider: PropertyProvider){
            voiceVoxUrl = propertyProvider.voiceVoxUrl
            nemoUrl = propertyProvider.nemoUrl
            voiceVoxSynthesisUrl = propertyProvider.voiceVoxSynthesisUrl
            nemoSynthesisUrl = propertyProvider.nemoSynthesisUrl
        }

        /**
         * voicevoxとの通信を行いデータを取得
         */
        @Throws
        fun getVoiceData(url: String, text: String, speaker: String, requestBody: RequestBody): Response{
            val audioQueryResponse = client.newCall(Request.Builder()
                .url(ConstantList.VOICEVOX_QUERY_TEMP.format(url, text, speaker))
                .post(requestBody)  // POST request with an empty body
                .build()).execute()

            if (!audioQueryResponse.isSuccessful) {
                throw IOException("Unexpected code $audioQueryResponse")
            }

            return audioQueryResponse
        }

        /**
         * VoiceTypeチェック
         * true: voiceVox
         * false: voiceVox Nemo
         */
        fun chkVoiceType(voiceType: String): Boolean{
            return voiceType == "0"
        }

        /**
         * voiceVoxURLを返す
         */
        fun getVoiceAudioUrl(voiceType: String): String{
            return if(chkVoiceType(voiceType)){
                voiceVoxUrl
            } else {
                nemoUrl
            }
        }

        /**
         * voiceVoxSynthesisUrlを返す
         */
        fun getVoiceSynthesisUrl(voiceType: String): String{
            return  if(chkVoiceType(voiceType)){
                voiceVoxSynthesisUrl
            }else{
                nemoSynthesisUrl
            }
        }

        /**
         * 読み方を変更
         */
        fun modifyKana(audioQuery: MutableMap<String, Any>, newKana: String){
            audioQuery["kana"] = newKana
        }

        fun getAccentPhrases(text: String): Response{
            return client.newCall(Request.Builder()
                .url(ConstantList.VOICEVOX_QUERY_TEMP.format("http://192.168.12.47:50021/accent_phrases", text, "3&is_kana=true"))
                .post(ConstantList.STR_BLANK.toRequestBody())  // POST request with an empty body
                .build()).execute()
        }
    }

    init{
        init(propertyProvider)
    }
}

@Component
class PropertyProvider {
    @Value("\${voicevox.url}") lateinit var voiceVoxUrl: String
    @Value("\${nemo.url}") lateinit var nemoUrl: String
    @Value("\${voicevox.synthesis-url}") lateinit var voiceVoxSynthesisUrl: String
    @Value("\${nemo.synthesis-url}") lateinit var nemoSynthesisUrl: String
}
