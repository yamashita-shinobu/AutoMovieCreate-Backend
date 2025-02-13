package com.example.testgpttovox.controller
import com.example.testgpttovox.exceptions.FileCreateException
import com.example.testgpttovox.service.CreateMovieService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://192.168.12.9:5173","http://localhost:5173"])
class CreateMovieController(
    private val createMovieService: CreateMovieService
) {

    @GetMapping("/createMovie")
    fun getCompletion(@RequestParam createTarget: String, @RequestParam searchTarget: String, @RequestParam userId: String): ResponseEntity<String> {
        try {
            val response = createMovieService.createMovie(createTarget, searchTarget, userId)
            return ResponseEntity.ok(response)
        } catch (ex: FileCreateException) {
            throw FileCreateException(ex.message?: "")
        }
    }
}