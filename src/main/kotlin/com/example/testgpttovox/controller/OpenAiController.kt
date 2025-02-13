package com.example.testgpttovox.controller
import com.example.testgpttovox.service.items.OpenAiClient
import com.example.testgpttovox.service.items.VoiceVoxClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class OpenAiController(
    private val openAiClient: OpenAiClient,
    private val voiceVoxClient: VoiceVoxClient
) {

    @GetMapping("/completion")
    fun getCompletion(@RequestParam prompt: String, @RequestParam maxTokens: Int): String? {
        val completion = openAiClient.getCompletion(prompt, maxTokens,"")
        completion?.let {
            val audioData = voiceVoxClient.textToSpeech(it,"","")
            audioData?.let { data ->
            }
        }
        return completion
    }
}