package com.dublesy.userservice.contoller

import com.dublesy.userservice.model.*
import com.dublesy.userservice.service.UserService
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.io.File

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {

    @PostMapping("/signup")
    suspend fun signUp(@RequestBody request: SignUpRequest) {
        userService.signUp(request)
    }

    @PostMapping("/signin")
    suspend fun signIn(@RequestBody signInRequest: SignInRequest) : SignInResponse {
        return userService.signIn(signInRequest)
    }

    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun logout(@AuthToken token : String) {
        userService.logout(token)
    }

    @GetMapping("/me")
    suspend fun get(
        @AuthToken token : String
    ) : MeResponse {
        return MeResponse(userService.getByToken(token))
    }

     @GetMapping("/{userId}/username")
     suspend fun getUsernamke(@PathVariable userId: Long) : Map<String, String> {
         return mapOf("reporter" to userService.get(userId).username)
     }

    @PostMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun edit(
        @PathVariable id: Long,
        @ModelAttribute request: UserEditRequest,
        @AuthToken token: String,
        @RequestPart("profileUrl") filePart: FilePart
    ) {

        val orgFilename = filePart.filename()
        var filename: String? = null
        if(orgFilename.isNotEmpty()) {
            // adb.jpg  , a.b.c.jpg 마지막 .
            val ext = orgFilename.substring(orgFilename.lastIndexOf(".") + 1)
            filename = "${id}.${ext}" // 1.jpg, 2.jpg
            val file = File(ClassPathResource("/images/").file, filename)
            filePart.transferTo(file).awaitSingleOrNull()
        }

        userService.edit(token, request.username, filename)


    }


}