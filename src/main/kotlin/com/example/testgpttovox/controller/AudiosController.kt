package com.example.testgpttovox.controller

import com.example.testgpttovox.dto.AudioInsertInputDto
import com.example.testgpttovox.service.InsertAudioService
import com.example.testgpttovox.service.InsertVideoService
import com.example.testgpttovox.util.StringUtils.Companion.pathToString
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://192.168.12.9:5173", "http://localhost:5173"])
class AudiosController(
    private val insertAudioService: InsertAudioService,
) {

    val itemPath = "C:\\Users\\yama\\createMovie\\items\\bgm"

    @PostMapping("/insertAudio")
    fun execution(@RequestBody audioInsertInputDto: AudioInsertInputDto) {
        try {
            insertAudioService.insert(audioInsertInputDto)
        } catch (ex: Exception) {
            throw RuntimeException(ex.message ?: "")
        }
    }

    @GetMapping("/getPlayAudio")
    fun getPlayAudio(@RequestParam audioName: String): ResponseEntity<ByteArray> {
        try {
            val file = File(pathToString(itemPath, audioName)).readBytes()
            return ResponseEntity.ok(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ResponseEntity.ok("".toByteArray())
    }

    @GetMapping("/loadBGMName")
    fun getBGMName(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(File(itemPath).listFiles()?.map{it.name})
    }
}