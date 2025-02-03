package org.chewing.v1.support

import org.chewing.v1.model.user.UserId
import org.chewing.v1.util.security.CurrentUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/private")
class TestPrivateController() {
    @PostMapping("")
    fun handleTestRequest(
        @CurrentUser userId: UserId,
    ): ResponseEntity<Any> {
        println(userId)
        return ResponseEntity.ok().build()
    }
}
