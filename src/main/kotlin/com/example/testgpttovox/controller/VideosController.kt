package com.example.testgpttovox.controller

import com.example.testgpttovox.repository.CreateVideoInfoRepository
import com.example.testgpttovox.repository.CreateVideosRepository
import com.example.testgpttovox.service.InsertVideoService
import com.example.testgpttovox.service.SelectVideoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://192.168.12.9:5173","http://localhost:5173"])
class VideosController(
    private val insertVideoService: InsertVideoService,
    private val selectVideoService: SelectVideoService,
    private val createVideoInfoRepository: CreateVideoInfoRepository,
    private val createVideosRepository: CreateVideosRepository,
) {

    @GetMapping("/insertVideo")
    fun execution() {
        try {
            insertVideoService.insert()
        } catch (ex: Exception) {
            throw RuntimeException(ex.message?: "")
        }
    }

    @GetMapping("/selectVideo")
    fun exe(@RequestParam userId: String, @RequestParam videoId: String): ResponseEntity<List<Map<String, Any>>>{
        val response = selectVideoService.select(userId, videoId)
            return ResponseEntity(response, HttpStatus.OK)
    }
}