package tech.espero.gruber.fullstackchallenge.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*


@Component
class JwtTokenUtil {
    private val serialVersionUID = -2550185165626007488L
    val JWT_TOKEN_VALIDITY = (5 * 60 * 60).toLong()

    @Value("\${jwt.secret}")
    private val secret: String? = null

    //retrieve username from jwt token
    fun getUsernameFromToken(token: String?): String {
        return getClaimFromToken(token) { it.subject }
    }

    //retrieve expiration date from jwt token
    fun getExpirationDateFromToken(token: String?): Date {
        return getClaimFromToken(token) { it.expiration }
    }

    fun <T> getClaimFromToken(token: String?, claimsResolver: (Claims) -> T): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver(claims)
    }

    //for retrieveing any information from token we will need the secret key
    private fun getAllClaimsFromToken(token: String?): Claims {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
    }

    //check if the token has expired
    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    //generate token for user
    fun generateToken(userDetails: UserDetails): String {
        val claims: Map<String, Any> = HashMap()
        return doGenerateToken(claims, userDetails.username)
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private fun doGenerateToken(claims: Map<String, Any>, subject: String): String {
        val keyBytes = Decoders.BASE64.decode(secret)
        val key: Key = Keys.hmacShaKeyFor(keyBytes)

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
            .signWith(key).compact()
    }

    //validate token
    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username: String = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }
}
