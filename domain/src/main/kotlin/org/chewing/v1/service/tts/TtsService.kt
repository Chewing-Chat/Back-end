package org.chewing.v1.service.tts

import org.chewing.v1.external.ExternalTtsClient
import org.chewing.v1.model.media.Media
import org.springframework.stereotype.Service

@Service
class TtsService(
    private val externalTtsClient: ExternalTtsClient,
) {
    suspend fun generateTtsFile(text: String, speaker: String): Media {
        return externalTtsClient.generateTts(text, speaker)
    }
}
