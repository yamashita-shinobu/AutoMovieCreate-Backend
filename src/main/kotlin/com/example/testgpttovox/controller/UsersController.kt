package com.example.testgpttovox.controller
import com.example.testgpttovox.entity.CreateVideoSamuneInfoEntity
import com.example.testgpttovox.repository.UserInfoRepository
import com.example.testgpttovox.service.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.Base64

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://192.168.12.9:5173","http://localhost:5173"])
class UsersController(
    private val usersService: UsersService,
) {

    @GetMapping("/login")
    fun login(@RequestParam userId: String, @RequestParam userName: String, @RequestParam email: String) {
        try {
            usersService.login(userId, userName,email)
        } catch (ex: Exception) {
            throw RuntimeException(ex.message?: "")
        }
    }

}