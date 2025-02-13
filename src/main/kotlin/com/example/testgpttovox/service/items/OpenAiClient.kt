package com.example.testgpttovox.service.items

import com.example.testgpttovox.service.BaseService
import com.example.testgpttovox.service.CreateMovieService
import com.example.testgpttovox.util.MsgSource
import okhttp3.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class OpenAiClient(
    @Value("\${openai.api-key}") val apiKey: String,
    @Value("\${openai.url}") val openUrl: String,
    messageSource: MsgSource,
) : BaseService(messageSource) {
    private val logger: Logger = LoggerFactory.getLogger(CreateMovieService::class.java)

    private val client = OkHttpClient()
    private val gson = Gson()

    fun getCompletion(prompt: String, maxTokens: Int, version: String): String? {
        return logExecution("OpenAiClient_getCompletion") {
            val jsonMap = mapOf(
                "model" to version, // 使用するモデルを指定
                "messages" to listOf(mapOf("role" to "user", "content" to prompt)),
                "max_tokens" to maxTokens
            )
            val json = gson.toJson(jsonMap)

            val mediaType = "application/json".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val request = Request.Builder()
                .url(openUrl)
                .addHeader("Authorization", "Bearer $apiKey")
                .post(requestBody)
                .build()

            var responseText: String? = null

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response: ${response.body?.string()}")

                responseText = response.body?.string()
            }

            extractMessageFromResponse(responseText?.replace("```json","")?.replace("```",""))
        }

    }

    private fun extractMessageFromResponse(responseText: String?): String? {
        return logExecution("extractMessageFromResponse") {
            val result = if (responseText == null) {
                null
            } else {
                val jsonObject = JsonParser.parseString(responseText).asJsonObject
                val choices = jsonObject.getAsJsonArray("choices")
                if (choices.size() > 0) {
                    val messageObject = choices[0].asJsonObject.getAsJsonObject("message")
                    messageObject.get("content").asString
                }else{
                    null
                }
            }
            result
        }
    }
}