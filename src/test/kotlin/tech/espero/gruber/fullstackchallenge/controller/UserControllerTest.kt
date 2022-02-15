package tech.espero.gruber.fullstackchallenge.controller

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest

@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {

    companion object {
        const val TEST_USERNAME = "testuser1"
        const val TEST_PASSWORD = "abc123"
    }

    @Test
    fun testRegister() {

    }
}