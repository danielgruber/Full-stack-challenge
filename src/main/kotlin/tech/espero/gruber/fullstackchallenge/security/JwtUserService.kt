package tech.espero.gruber.fullstackchallenge.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import tech.espero.gruber.fullstackchallenge.service.UserService


@Service
class JwtUserDetailsService : UserDetailsService {
    @Autowired
    private lateinit var userService: UserService

    override fun loadUserByUsername(username: String): UserDetails {
        return userService.getByUsername(username) ?: throw UsernameNotFoundException("User not found with username: $username")
    }
}
