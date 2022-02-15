package tech.espero.gruber.fullstackchallenge.controller

import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.MockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import tech.espero.gruber.fullstackchallenge.exceptions.PasswordTooWeakException
import tech.espero.gruber.fullstackchallenge.model.User
import tech.espero.gruber.fullstackchallenge.repository.UserRepository

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    companion object {
        const val TEST_USERNAME = "testuser1"
        const val TEST_PASSWORD = "abc123AB"
        const val TEST_PASSWORD_TOO_WEAK = "1234"
        val ROLE_SELLER = User.UserRole.SELLER.toString()
        val ROLE_BUYER = User.UserRole.BUYER.toString()
        val ROLE_INVALID = "INVALID"
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    /**
     * Tests if POST /users to register a user with BUYER role, valid username and password returns valid JSON result.
     */
    @Test
    fun testRegisterBuyer() {
        try {
            val json = JSONObject()
            json.put("username", TEST_USERNAME)
            json.put("userRole", ROLE_BUYER)
            json.put("password", TEST_PASSWORD)

            mockMvc.perform(
                post("/user").contentType(MediaType.APPLICATION_JSON).content(json.toString())
            ).andExpect(
                status().isCreated
            ).andExpect(
                content().contentType(MediaType.APPLICATION_JSON)
            ).andExpect(
                jsonPath("$.role").value(ROLE_BUYER)
            )
        } finally {
            userRepository.getByUsername(TEST_USERNAME)?.let {
                userRepository.delete(it)
            }
        }
    }

    /**
     * Tests if POST /users to register a user with SELLER role, valid username and password returns valid JSON result.
     */
    @Test
    fun testRegisterSeller() {
        try {
            val json = JSONObject()
            json.put("username", TEST_USERNAME)
            json.put("userRole", ROLE_SELLER)
            json.put("password", TEST_PASSWORD)

            mockMvc.perform(
                post("/user").contentType(MediaType.APPLICATION_JSON).content(json.toString())
            ).andExpect(
                status().isCreated
            ).andExpect(
                content().contentType(MediaType.APPLICATION_JSON)
            ).andExpect(
                jsonPath("$.role").value(ROLE_SELLER)
            )
        } finally {
            userRepository.getByUsername(TEST_USERNAME)?.let {
                userRepository.delete(it)
            }
        }
    }

    /**
     * Tests if POST /users to register a user with invalid role, valid username and password returns bad request.
     */
    @Test
    fun testRegisterInvalidRole() {
        try {
            val json = JSONObject()
            json.put("username", TEST_USERNAME)
            json.put("userRole", ROLE_INVALID)
            json.put("password", TEST_PASSWORD)

            mockMvc.perform(
                post("/user").
                contentType(MediaType.APPLICATION_JSON).
                content(json.toString())
            ).andExpect(
                status().isBadRequest
            ).andExpect(
                content().contentType(MediaType.APPLICATION_JSON)
            )
        } finally {
            userRepository.getByUsername(TEST_USERNAME)?.let {
                userRepository.delete(it)
            }
        }
    }

    /**
     * Tests if POST /users to register a user with duplicate username, valid role and password returns a conflict status.
     */
    @Test
    fun testRegisterDuplicateUsername() {
        try {
            val json = JSONObject()
            json.put("username", TEST_USERNAME)
            json.put("userRole", ROLE_BUYER)
            json.put("password", TEST_PASSWORD)

            // perform 1st request
            mockMvc.perform(
                post("/user").
                contentType(MediaType.APPLICATION_JSON).
                content(json.toString())
            ).andExpect(
                status().isCreated
            )

            mockMvc.perform(
                post("/user").
                contentType(MediaType.APPLICATION_JSON).
                content(json.toString())
            ).andExpect(
                status().isConflict
            ).andExpect(
                content().contentType(MediaType.APPLICATION_JSON)
            )
        } finally {
            userRepository.getByUsername(TEST_USERNAME)?.let {
                userRepository.delete(it)
            }
        }
    }

    /**
     * Tests if POST /users to register a user with weak password, valid role and username returns a bad request status.
     */
    @Test
    fun testRegisterWeakPassword() {
        try {
            val json = JSONObject()
            json.put("username", TEST_USERNAME)
            json.put("userRole", ROLE_BUYER)
            json.put("password", TEST_PASSWORD_TOO_WEAK)

            // perform 1st request
            mockMvc.perform(
                post("/user").
                contentType(MediaType.APPLICATION_JSON).
                content(json.toString())
            ).andExpect(
                status().isBadRequest
            ).andExpect(
                content().contentType(MediaType.APPLICATION_JSON)
            ).andExpect(
                jsonPath("$.exception").value(PasswordTooWeakException::class.simpleName)
            )
        } finally {
            userRepository.getByUsername(TEST_USERNAME)?.let {
                userRepository.delete(it)
            }
        }
    }

    /**
     * Tests if POST /users to register a user with empty username, valid role and password returns a bad request status.
     */
    @Test
    fun testRegisterEmptyUser() {
        try {
            val json = JSONObject()
            json.put("username", "")
            json.put("userRole", ROLE_BUYER)
            json.put("password", TEST_PASSWORD)

            // perform 1st request
            mockMvc.perform(
                post("/user").
                contentType(MediaType.APPLICATION_JSON).
                content(json.toString())
            ).andExpect(
                status().isBadRequest
            ).andExpect(
                content().contentType(MediaType.APPLICATION_JSON)
            )
        } finally {
            userRepository.getByUsername("")?.let {
                userRepository.delete(it)
            }
        }
    }
}