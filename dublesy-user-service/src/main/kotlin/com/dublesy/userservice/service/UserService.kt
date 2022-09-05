package com.dublesy.userservice.service

import com.auth0.jwt.interfaces.DecodedJWT
import com.dublesy.userservice.config.JWTProperties
import com.dublesy.userservice.domain.entity.User
import com.dublesy.userservice.domain.repository.UserRepository
import com.dublesy.userservice.exception.InvalidJwdTokenException
import com.dublesy.userservice.exception.PasswordNOtMatchedException
import com.dublesy.userservice.exception.UserExistsException
import com.dublesy.userservice.exception.UserNotFoundException
import com.dublesy.userservice.model.SignInRequest
import com.dublesy.userservice.model.SignInResponse
import com.dublesy.userservice.model.SignUpRequest
import com.dublesy.userservice.utils.BCryptUtils
import com.dublesy.userservice.utils.JWTClaim
import com.dublesy.userservice.utils.JwtUtils
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtProperties: JWTProperties,
    private val cacheManager: CoroutineCacheManager<User>,

) {

    companion object {
        private val CACHE_TTL = Duration.ofMinutes(1)
    }

    suspend fun signUp(signUpRequest: SignUpRequest) {
        with(signUpRequest) {
            userRepository.findByEmail(email)?.let {
                throw UserExistsException()
            }

            val user = User(
                email = email,
                password = BCryptUtils.hash(password),
                username =  username,
            )

            userRepository.save(user)
        }

    }

    suspend fun signIn(signInRequest: SignInRequest): SignInResponse {
        return with(userRepository.findByEmail(signInRequest.email) ?: throw UserNotFoundException()) {
            val verified = BCryptUtils.verify(signInRequest.password, password)
            if(!verified) {
                throw PasswordNOtMatchedException()
            }

            val jwtClaim = JWTClaim(
                userId = id!!,
                email = email,
                profileUrl = profileUrl,
                username = username
            )

            val token = JwtUtils.createToken(jwtClaim, jwtProperties)

            cacheManager.awaitPut(key = token, value = this, ttl = CACHE_TTL )

            SignInResponse(
                email = email,
                username = username,
                token = token
            )
        }

    }

    suspend fun logout(token: String) {

        cacheManager.awaitEvict(token)
    }

    suspend fun getByToken(token: String): User {
       val cachedUser =  cacheManager.awaitGetOrPut(key = token, ttl = CACHE_TTL) {
            //캐시가 유효하지 않은 경우 동작
            val decodedJWT: DecodedJWT = JwtUtils.decode(token, jwtProperties.secret, jwtProperties.issuer)

            var userId: Long = decodedJWT.claims["userId"]?.asLong() ?: throw InvalidJwdTokenException()
            get(userId)
        }
        return cachedUser;
    }

    suspend fun get(userId: Long) : User {
        return userRepository.findById(userId) ?: throw UserNotFoundException()
    }

    suspend fun edit(token: String, username: String, profileUrl: String?): User {
        val user = getByToken(token)

        val newUser = user.copy(username = username, profileUrl = profileUrl ?: user.profileUrl)
        return userRepository.save(newUser).also {
            cacheManager.awaitPut(key = token, value = it, ttl = CACHE_TTL)
        }
    }

}