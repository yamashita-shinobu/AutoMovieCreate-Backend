package com.example.testgpttovox.controller
import com.example.testgpttovox.entity.CreateVideoSamuneInfoEntity
import com.example.testgpttovox.service.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.Base64

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://192.168.12.9:5173","http://localhost:5173"])
class ImagesController(
    private val insertImageService: InsertImageService,
    private val getVideoSamuneService: GetVideoSamuneService,
    private val getImageService: GetImageService,
) {

    @GetMapping("/insertImage")
    fun execution() {
        try {
            insertImageService.insert()
        } catch (ex: Exception) {
            throw RuntimeException(ex.message?: "")
        }
    }

    @GetMapping("/selectImage")
    fun exe(@RequestParam userId: String): ResponseEntity<List<Map<String, Any>>>{
        val mediaList = getVideoSamuneService.getSamune(userId)
        val response = mediaList.map { media ->
            mapOf(
                "id" to media.videoId,
                "filename" to media.title,
                "filetype" to "jpeg",
                "data" to Base64.getEncoder().encodeToString(media.imageData)
            )
        }
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/getImage")
    fun getImage(@RequestParam createTarget: String){
        getImageService.getImage(createTarget,"")
    }
}