package tech.espero.gruber.fullstackchallenge.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import tech.espero.gruber.fullstackchallenge.security.JwtTokenUtil
import tech.espero.gruber.fullstackchallenge.security.JwtUserDetailsService


@RestController
class AuthController {
    @Autowired
    private  lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @Autowired
    private lateinit var userDetailsService: JwtUserDetailsService

    data class JwtRequest(val username: String, val password: String)
    data class JwtResponse(val jwttoken: String)

    @RequestMapping(value = ["/authenticate"], method = [RequestMethod.POST])
    fun createAuthenticationToken(@RequestBody authenticationRequest: JwtRequest): ResponseEntity<*>? {
        authenticate(authenticationRequest.username, authenticationRequest.password)
        val userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username)
        val token: String = jwtTokenUtil.generateToken(userDetails)
        return ResponseEntity.ok<Any>(JwtResponse(token))
    }

    private fun authenticate(username: String, password: String) {
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (e: DisabledException) {
            throw Exception("USER_DISABLED", e)
        } catch (e: BadCredentialsException) {
            throw Exception("INVALID_CREDENTIALS", e)
        }
    }
}
