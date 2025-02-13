package com.example.testgpttovox.controller

import com.example.testgpttovox.dto.EditMovieInfoDto
import com.example.testgpttovox.dto.EditMovieSeneInfoDto
import com.example.testgpttovox.entity.CreateVideoSamuneInfoEntity
import com.example.testgpttovox.repository.CreateVideoSamuneInfoRepository
import com.example.testgpttovox.repository.CreateVideosRepository
import com.example.testgpttovox.repository.EditVideoInfoRepository
import com.example.testgpttovox.util.StringUtils.Companion.getExtension
import com.example.testgpttovox.util.StringUtils.Companion.viewImageStr
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://192.168.12.9:5173", "http://localhost:5173"])
class DeleteController(
    val createVideosRepository: CreateVideosRepository,
    val createVideoSamuneInfoRepository: CreateVideoSamuneInfoRepository,
    ) {
    @GetMapping("/deleteSamune")
    fun deleteSamune(@RequestParam userId: String) {

        val videoData = createVideosRepository.selectByUser(userId)
        videoData.forEach {
            if(createVideoSamuneInfoRepository.selectExists(userId, it.videoId)){
                createVideosRepository.delete(it)
            }
        }
    }

}