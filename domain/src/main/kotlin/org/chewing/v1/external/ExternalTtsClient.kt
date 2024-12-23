package org.chewing.v1.external

import org.chewing.v1.model.media.Media

interface ExternalTtsClient {
    suspend fun generateTts(text: String, speaker: String): Media
}
