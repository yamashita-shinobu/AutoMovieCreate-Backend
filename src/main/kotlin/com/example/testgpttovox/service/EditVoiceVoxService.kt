package com.example.testgpttovox.service

import com.example.testgpttovox.entity.CreateVideoSamuneInfoEntity
import com.example.testgpttovox.enum.MediaType
import com.example.testgpttovox.repository.CreateVideoSamuneInfoRepository
import com.example.testgpttovox.service.items.VoiceVoxClient
import com.example.testgpttovox.util.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

@Service
class EditVoiceVoxService(
    val voiceVoxClient: VoiceVoxClient,
    messageSource: MsgSource,
) : BaseService(messageSource) {

    /**
     * 音声データを取得
     */
    fun getAudioData(text: String): ByteArray {
        return logExecution("getAudioData") {
            val encode = URLEncoder.encode(text, StandardCharsets.UTF_8.toString())
            voiceVoxClient.textToSpeech(encode, "3", "0") ?: "".toByteArray()
        }
    }

    /**
     * 読み方変更
     */
    fun changeKana(text: String): ByteArray? {
        return logExecution("changeKana") {
//            val audioQueryJson =
//                jacksonObjectMapper().readValue(Files.readString(Paths.get("src/main/resources/testFile/test.txt")),
//                    object : TypeReference<MutableMap<String, Any>>() {})

            val text2 = "コノ'/シュ'ワ、ヒジョオニ'/コ'オカツデ/イキ'ル/ス'ベニ/スグレ'タ/セ'エブツノ/_ヒト'ツデ_ス"
//            VoiceVoxUtils.modifyKana(audioQueryJson, text2)
//            audioQueryJson["kana"] =
//                "コノ'/シュ'ワ、ヒジョオニ'/コ'オカツデ/イキ'ル/ス'ベニ/スグレ'タ/セ'エブツノ/_ヒト'ツデ_ス"

            val audioQueryResponse2 =VoiceVoxUtils.getAccentPhrases(text2)

//            val audioQueryJson2 =
//                jacksonObjectMapper().readValue(audioQueryResponse2.body?.string() ?: "",
//                    object : TypeReference<MutableMap<String, Any>>() {})

            val test = audioQueryResponse2.body?.string()

            val string = """
                {"accent_phrases":$test,"speedScale":"1.3","pitchScale":0.0,"intonationScale":1.0,"volumeScale":1.0,"prePhonemeLength":0.1,"postPhonemeLength":0.1,"pauseLength":null,"pauseLengthScale":1.0,"outputSamplingRate":24000,"outputStereo":false,"kana":"$text2"}
            """.trimIndent()
            println(string)

            val audioQueryJson2 =
                jacksonObjectMapper().readValue(string,
                    object : TypeReference<MutableMap<String, Any>>() {})

            VoiceVoxUtils.getVoiceData(
                VoiceVoxUtils.getVoiceSynthesisUrl("0"),// voiceVoxSynthesisURLを取得
                text,
                "3",
                jacksonObjectMapper().writeValueAsString(audioQueryJson2).toRequestBody(MediaType.JSON.value.toMediaTypeOrNull()),
            ).body?.bytes()
        }
    }
}