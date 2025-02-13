package com.example.testgpttovox.controller
import com.example.testgpttovox.repository.CreateVideoInfoRepository
import com.example.testgpttovox.repository.CreateVideosRepository
import com.example.testgpttovox.service.EditVoiceVoxService
import com.example.testgpttovox.service.InsertVideoService
import com.example.testgpttovox.service.SelectVideoService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://192.168.12.9:5173","http://localhost:5173"])
class EditVoiceVoxController(
    private val editVoiceVoxService: EditVoiceVoxService,
) {

    @GetMapping("/editChangeText")
    fun editChangeText(@RequestParam text: String): ResponseEntity<ByteArray> {
        try {
            val decodeTxt = URLDecoder.decode(text, StandardCharsets.UTF_8.toString())
            val headers = HttpHeaders()
            headers.set(HttpHeaders.CONTENT_TYPE, "audio/wav")
            return  ResponseEntity(editVoiceVoxService.getAudioData(decodeTxt), headers, HttpStatus.OK)
        } catch (ex: Exception) {
            throw RuntimeException(ex.message?: "")
        }
    }

    @GetMapping("/changeKana")
    fun changeKana(@RequestParam text: String): ResponseEntity<ByteArray> {
            val headers = HttpHeaders()
            headers.set(HttpHeaders.CONTENT_TYPE, "audio/wav")
            return  ResponseEntity(editVoiceVoxService.changeKana(text), headers, HttpStatus.OK)
    }
}