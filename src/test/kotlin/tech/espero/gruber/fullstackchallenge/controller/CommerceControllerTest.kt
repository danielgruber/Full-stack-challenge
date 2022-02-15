package tech.espero.gruber.fullstackchallenge.controller

import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import tech.espero.gruber.fullstackchallenge.model.User
import tech.espero.gruber.fullstackchallenge.repository.ProductRepository
import tech.espero.gruber.fullstackchallenge.repository.UserRepository
import tech.espero.gruber.fullstackchallenge.service.CommerceService
import java.util.*

@AutoConfigureMockMvc
@SpringBootTest
class CommerceControllerTest {

    companion object {
        const val TEST_USERNAME_BUYER = "buyer1"
        const val TEST_USERNAME_SELLER = "seller1"
        const val TEST_PASSWORD = "abc123AB"

        val ROLE_SELLER = User.UserRole.SELLER.toString()
        val ROLE_BUYER = User.UserRole.BUYER.toString()

        val PRODUCT_ID = UUID.randomUUID()
        const val PRODUCT_NAME = "Product 1"
        const val PRODUCT_COST = 10
        const val PRODUCT_AMOUNT = 5
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var commerceService: CommerceService

    private lateinit var sellerJwt: String
    private lateinit var buyerJwt: String

    @BeforeEach
    fun ensureUsers() {
        val jsonBuyer = JSONObject()
        jsonBuyer.put("username", TEST_USERNAME_BUYER)
        jsonBuyer.put("userRole", ROLE_BUYER)
        jsonBuyer.put("password", TEST_PASSWORD)

        val jsonSeller = JSONObject()
        jsonSeller.put("username", TEST_USERNAME_SELLER)
        jsonSeller.put("userRole", ROLE_SELLER)
        jsonSeller.put("password", TEST_PASSWORD)

        // Register Buyer
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/user").contentType(MediaType.APPLICATION_JSON).content(jsonBuyer.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isCreated
        )

        // Regiser Seller
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/user").contentType(MediaType.APPLICATION_JSON).content(jsonSeller.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isCreated
        )

        // Login Buyer
        val buyerAuthResult = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/authenticate").contentType(MediaType.APPLICATION_JSON).content(jsonBuyer.toString())
        ).andReturn()
        val buyerJsonAuthResult = JSONObject(buyerAuthResult.response.contentAsString)
        buyerJwt = buyerJsonAuthResult.getString("token")

        // Login Seller
        val sellerAuthResult = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/authenticate").contentType(MediaType.APPLICATION_JSON).content(jsonSeller.toString())
        ).andReturn()
        val sellerJsonAuthResult = JSONObject(sellerAuthResult.response.contentAsString)
        sellerJwt = sellerJsonAuthResult.getString("token")

        // Create product
        val jsonProduct = JSONObject()
        jsonProduct.put("name", PRODUCT_NAME)
        jsonProduct.put("cost", PRODUCT_COST)
        jsonProduct.put("amountAvailable", PRODUCT_AMOUNT)

        // Create Product
        mockMvc.perform(
            RestDocumentationRequestBuilders
                .put("/product/$PRODUCT_ID")
                .header("Authorization", "Bearer $sellerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonProduct.toString()
            )
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        )
    }

    @AfterEach
    fun cleanup() {
        productRepository.findByIdOrNull(PRODUCT_ID)?.let {
            productRepository.delete(it)
        }

        userRepository.getByUsername(TEST_USERNAME_BUYER)?.let {
            userRepository.delete(it)
        }

        userRepository.getByUsername(TEST_USERNAME_SELLER)?.let {
            userRepository.delete(it)
        }
    }

    //region: Deposit tests

    /**
     * Tests depositing invalid coins.
     */
    @Test
    fun testDepositInvalidCoins() {
        // Put enough coins
        val coinRequest = JSONObject()
        coinRequest.put("coins", JSONArray(arrayOf(1, 9)))

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/deposit")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(coinRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isBadRequest
        )
    }

    /**
     * Tests depositing valid coins returns ok and right amount of deposited money.
     */
    @Test
    fun testDepositValidCoins() {
        // Put enough coins
        val coinRequest = JSONObject()
        coinRequest.put("coins", JSONArray(commerceService.allowedCoins))

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/deposit")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(coinRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.depositCents").value(
                commerceService.allowedCoins.fold(0) { acc, i -> acc + i }
            )
        )
    }

    /**
     * Tests depositing empty coins works.
     */
    @Test
    fun testDeposit0() {
        // Put enough coins
        val coinRequest = JSONObject()
        coinRequest.put("coins", JSONArray())

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/deposit")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(coinRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        )
    }

    //endregion

    //region: Buy Tests

    /**
     * Tests buying a product without inserting coins throws 402.
     */
    @Test
    fun testBuyWithoutMoney() {
        val buyRequest = JSONObject()
        buyRequest.put("productId", PRODUCT_ID)
        buyRequest.put("productAmount", 1)

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/buy")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buyRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isPaymentRequired
        )
    }

    /**
     * Tests buying a product without inserting exact coins will return no change and right total and product.
     */
    @Test
    fun testBuyWithExactMoney() {
        val buyRequest = JSONObject()
        buyRequest.put("productId", PRODUCT_ID)
        buyRequest.put("productAmount", 1)

        // Put enough coins
        val coinRequest = JSONObject()
        coinRequest.put("coins", JSONArray(arrayOf(5, 5)))

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/deposit")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(coinRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        )

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/buy")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buyRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.change").isEmpty
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.total").value(10)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.product.productName").value(PRODUCT_NAME)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.product.amountAvailable").value(PRODUCT_AMOUNT - 1)
        )
    }

    /**
     * Tests buying a product with inserting more coins will return right change and right total and product.
     */
    @Test
    fun testBuyWithMoreMoney() {
        val buyRequest = JSONObject()
        buyRequest.put("productId", PRODUCT_ID)
        buyRequest.put("productAmount", 1)

        // Put enough coins
        val coinRequest = JSONObject()
        coinRequest.put("coins", JSONArray(arrayOf(5, 5, 100)))

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/deposit")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(coinRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        )

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/buy")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buyRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.change").value(100)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.total").value(10)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.product.productName").value(PRODUCT_NAME)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.product.amountAvailable").value(PRODUCT_AMOUNT - 1)
        )
    }

    /**
     * Tests buying a product with inserting more coins will return right change and right total and product.
     */
    @Test
    fun testBuyWithComplexChangeMoney() {
        val buyRequest = JSONObject()
        buyRequest.put("productId", PRODUCT_ID)
        buyRequest.put("productAmount", 4)

        // Put enough coins
        val coinRequest = JSONObject()
        coinRequest.put("coins", JSONArray(arrayOf(100)))

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/deposit")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(coinRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        )

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/buy")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buyRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.change").value(arrayOf(50, 10))
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.total").value(10)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.product.productName").value(PRODUCT_NAME)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.product.amountAvailable").value(PRODUCT_AMOUNT - 4)
        )
    }

    /**
     * Tests buying a product without inserting sufficient coins will return payment required.
     */
    @Test
    fun testBuyWithTooLessMoney() {
        val buyRequest = JSONObject()
        buyRequest.put("productId", PRODUCT_ID)
        buyRequest.put("productAmount", 4)

        // Put enough coins
        val coinRequest = JSONObject()
        coinRequest.put("coins", JSONArray(arrayOf(5, 5)))

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/deposit")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(coinRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        )

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/buy")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buyRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isPaymentRequired
        )
    }

    /**
     * Tests buying a product in too big amount.
     */
    @Test
    fun testBuyTooMany() {
        val buyRequest = JSONObject()
        buyRequest.put("productId", PRODUCT_ID)
        buyRequest.put("productAmount", 6)

        // Put enough coins
        val coinRequest = JSONObject()
        coinRequest.put("coins", JSONArray(arrayOf(100)))

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/deposit")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(coinRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        )

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/buy")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(buyRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isBadRequest
        )
    }

    //endregion

    //region: Reset Tests

    /**
     * Tests reset resets deposit when having added coins.
     */
    @Test
    fun testResetResetsCoins() {
        val coinRequest = JSONObject()
        coinRequest.put("coins", JSONArray(arrayOf(5, 10)))

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/deposit")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(coinRequest.toString())
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        )

        assertEquals(15, userRepository.getByUsername(TEST_USERNAME_BUYER)?.depositCents)

        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/reset")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        )

        assertEquals(0, userRepository.getByUsername(TEST_USERNAME_BUYER)?.depositCents)
    }

    /**
     * Tests reset resets nothing but returns ok if nothing is added.
     */
    @Test
    fun testResetIsOk() {
        mockMvc.perform(
            RestDocumentationRequestBuilders
                .post("/reset")
                .header("Authorization", "Bearer $buyerJwt")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.status().isOk
        )

        assertEquals(0, userRepository.getByUsername(TEST_USERNAME_BUYER)?.depositCents)
    }

    //endregion
}
