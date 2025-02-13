package com.example.testgpttovox.controller

import com.example.testgpttovox.dto.EditMovieInfoDto
import com.example.testgpttovox.dto.EditMovieSeneInfoDto
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
class TestEditController(
    val editVideoInfoRepository: EditVideoInfoRepository,
) {
    val itemPath = "C:\\Users\\yama\\createMovie\\items\\"
    val createPath = "C:\\Users\\yama\\createMovie\\create\\"

    @GetMapping("/testEdit")
    fun testEdit(): ResponseEntity<List<Map<String, Any>>> {
        val imagePath = Path.of("${itemPath}commentFrame")

        // ディレクトリが存在しない場合、空リストを返す
        if (!Files.exists(imagePath) || !Files.isDirectory(imagePath)) {
            return ResponseEntity.ok(emptyList())
        }

        val files = File(imagePath.toString()).listFiles { file ->
            file.isFile && (file.extension.lowercase() in listOf("jpg", "jpeg", "png", "gif"))
        }?.map { it } ?: emptyList()

        val response = files.map { file ->
            mapOf(
                "filename" to file.name,
                "filetype" to when (file.name.lowercase()) {
                    "jpg", "jpeg" -> "jpeg"
                    "png" -> "png"
                    "gif" -> "gif"
                    else -> MediaType.APPLICATION_OCTET_STREAM
                },
                "data" to Base64.getEncoder().encodeToString(file.readBytes()),
            )
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/getBms")
    fun getBms(): ResponseEntity<List<Map<String, Any>>> {

        val imagePath = Path.of("${itemPath}bms")

        // ディレクトリが存在しない場合、空リストを返す
        if (!Files.exists(imagePath) || !Files.isDirectory(imagePath)) {
            return ResponseEntity.ok(emptyList())
        }

        val files = File(imagePath.toString()).listFiles { file ->
            file.isFile && (file.extension.lowercase() in listOf("jpg", "jpeg", "png", "gif"))
        }?.map { it } ?: emptyList()

        val response = files.map { file ->
            mapOf(
                "filename" to file.name,
                "filetype" to when (file.name.lowercase()) {
                    "jpg", "jpeg" -> "jpeg"
                    "png" -> "png"
                    "gif" -> "gif"
                    else -> MediaType.APPLICATION_OCTET_STREAM
                },
                "data" to Base64.getEncoder().encodeToString(file.readBytes()),
            )
        }
        return ResponseEntity.ok(response)
    }

    @GetMapping("/getEditMoveInfo")
    fun getEditMoveInfo(@RequestParam userId: String, @RequestParam videoId: String): ResponseEntity<EditMovieInfoDto> {
        val videoInfo = editVideoInfoRepository.selectByKey(userId, videoId)

        val mainImageNames =
            jacksonObjectMapper().readValue(videoInfo.mainImages, object : TypeReference<List<String>>() {})
        val mainImages = mainImageNames.map { mainImageName ->
            val mainImagePath = Path.of("${createPath}\\${userId}\\${videoId}\\${mainImageName}")
            File(mainImagePath.toString())
        }

        val cmtLists =
            jacksonObjectMapper().readValue(videoInfo.comments, object : TypeReference<List<List<String>>>() {})

        val sceneItems = mainImageNames.mapIndexed { index, imageName ->
            EditMovieSeneInfoDto(
                mainImageName = imageName,
                mainImage = viewImageStr("jpeg", mainImages[index]),
                comment = cmtLists[index]
            )
        }

        val imagePath = Path.of("${itemPath}bms\\${videoInfo.bmsName}")
        val fileImage = File(imagePath.toString())

        val cmtFramePath = Path.of("${itemPath}commentFrame\\${videoInfo.cmtFrameName}")
        val cmtFrameFile = File(cmtFramePath.toString())
        return ResponseEntity.ok(
            EditMovieInfoDto(
                userId = userId,
                videoId = videoId,
                bmsImage = viewImageStr(getExtension(videoInfo.bmsName), fileImage),
                bmsName = fileImage.name,
                cmtFrameName = videoInfo.cmtFrameName,
                cmtFrameImage = viewImageStr(getExtension(videoInfo.cmtFrameName), cmtFrameFile),
                bgmName = videoInfo.bgmName,
                sceneItems = sceneItems,
            )
        )
    }

    @GetMapping("/loadMainImage")
    fun loadMainImage(
        @RequestParam userId: String,
        @RequestParam videoId: String
    ): ResponseEntity<List<Map<String, Any>>> {

        val imagePath = Path.of("${createPath}${userId}\\${videoId}")

        println("imagePath:$imagePath")
        // ディレクトリが存在しない場合、空リストを返す
        if (!Files.exists(imagePath) || !Files.isDirectory(imagePath)) {
            return ResponseEntity.ok(emptyList())
        }

        val files = File(imagePath.toString()).listFiles { file ->
            file.isFile && (file.extension.lowercase() in listOf("jpg", "jpeg", "png", "gif"))
        }?.map { it } ?: emptyList()

        val response = files.map { file ->
            mapOf(
                "filename" to file.name,
                "filetype" to when (file.name.lowercase()) {
                    "jpg", "jpeg" -> "jpeg"
                    "png" -> "png"
                    "gif" -> "gif"
                    else -> MediaType.APPLICATION_OCTET_STREAM
                },
                "data" to Base64.getEncoder().encodeToString(file.readBytes()),
            )
        }
        return ResponseEntity.ok(response)
    }
}