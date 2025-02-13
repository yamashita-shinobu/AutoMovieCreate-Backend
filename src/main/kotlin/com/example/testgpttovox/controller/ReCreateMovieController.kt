package com.example.testgpttovox.controller
import com.example.testgpttovox.dto.EditMovieInfoDto
import com.example.testgpttovox.exceptions.FileCreateException
import com.example.testgpttovox.service.CreateMovieService
import com.example.testgpttovox.service.ReCreateMovieService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://192.168.12.9:5173","http://localhost:5173"])
class ReCreateMovieController(
    private val reCreateMovieService: ReCreateMovieService
) {

    @PostMapping("/reCreateMovie")
    fun getCompletion(@RequestBody editMovieInfoDto: EditMovieInfoDto): ResponseEntity<String> {
        try {
            val response = reCreateMovieService.reCreateMovie(editMovieInfoDto)
            return ResponseEntity.ok(response)
        } catch (ex: FileCreateException) {
            throw FileCreateException(ex.message?: "")
        }
    }
}