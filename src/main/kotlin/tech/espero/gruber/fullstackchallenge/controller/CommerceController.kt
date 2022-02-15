package tech.espero.gruber.fullstackchallenge.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tech.espero.gruber.fullstackchallenge.model.User
import tech.espero.gruber.fullstackchallenge.service.CommerceService
import java.util.*

@RestController
class CommerceController {
    @Autowired
    private lateinit var commerceService: CommerceService

    data class DepositRequest(
        val coins: List<Int>
    )

    data class BuyRequest(
        val productId: UUID,
        val productAmount: Int
    )

    @PostMapping("/buy")
    fun buy(@RequestBody buyRequest: BuyRequest): ResponseEntity<CommerceService.BuyFeedback> {
        return ResponseEntity.ok(commerceService.buy(buyRequest.productId, buyRequest.productAmount))
    }

    @PostMapping("/reset")
    fun reset(): ResponseEntity<User> {
        return ResponseEntity.ok(commerceService.reset())
    }

    @PostMapping("/deposit")
    fun deposit(@RequestBody depositRequest: DepositRequest): ResponseEntity<User> {
        return ResponseEntity.ok(commerceService.deposit(depositRequest.coins))
    }
}
