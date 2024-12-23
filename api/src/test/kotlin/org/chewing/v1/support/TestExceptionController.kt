package org.chewing.v1.support

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/test")
class TestExceptionController(
    private val testExceptionService: TestExceptionService,
) {
    @PostMapping("")
    fun handleTestRequest(
        @RequestBody request: TestRequest,
        @RequestParam("test") test: String,
    ): ResponseEntity<Any> {
        testExceptionService.testException()
        return ResponseEntity.ok().build()
    }
}
