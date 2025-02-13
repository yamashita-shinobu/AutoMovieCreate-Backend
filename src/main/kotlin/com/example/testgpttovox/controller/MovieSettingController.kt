package com.example.testgpttovox.controller
import com.example.testgpttovox.dto.CreateVideoSettingDto
import com.example.testgpttovox.exceptions.FileCreateException
import com.example.testgpttovox.service.CreateMovieService
import com.example.testgpttovox.service.MovieSettingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://192.168.12.9:5173","http://localhost:5173"])
class MovieSettingController(
    private val movieSettingService: MovieSettingService
) {

    @GetMapping("/selectMovieSetting")
    fun getCompletion(@RequestParam userId: String): ResponseEntity<CreateVideoSettingDto> {
        try {
            return ResponseEntity.ok(movieSettingService.selectSetting(userId))
        } catch (ex: FileCreateException) {
            throw FileCreateException(ex.message?: "")
        }
    }

    @PutMapping("/updateMovieSetting")
    fun updateCompletion(
        @RequestParam chatGptVersion: String,
        @RequestParam voiceVoxType: String,
        @RequestParam voiceVoxNarration: String,
        @RequestParam userid: String,
    ): ResponseEntity<String> {
        try {
            movieSettingService.updateSetting(
                chatGptVersion,
                voiceVoxType,
                voiceVoxNarration,
                userid,
            )
            return ResponseEntity.ok("success")
        } catch (ex: FileCreateException) {
            throw FileCreateException(ex.message?: "")
        }
    }
}