package org.chewing.v1.controller.tts

import org.chewing.v1.service.tts.TtsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TtsController(
    private val ttsService: TtsService,
) {
    @GetMapping("/api/tts")
    suspend fun generateTts(
        @RequestParam text: String,
        @RequestParam(defaultValue = "nara") speaker: String,
    ): ResponseEntity<String> {
        val media = ttsService.generateTtsFile(text, speaker)
        return ResponseEntity.ok("File uploaded successfully: ${media.url}")
    }
}
