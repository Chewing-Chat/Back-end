package org.chewing.v1.jpaentity.emoticon

import jakarta.persistence.*
import org.chewing.v1.model.emoticon.EmoticonInfo
import java.util.*

@Entity
@Table(name = "emoticon", schema = "chewing")
internal class EmoticonJpaEntity(
    @Id
    private val emoticonId: String = UUID.randomUUID().toString(),
    private val url: String,
    private val name: String,
    private val emoticonPackId: String,
) {
    companion object {
        fun of(
            url: String,
            name: String,
            emoticonPackId: String,
        ): EmoticonJpaEntity {
            return EmoticonJpaEntity(
                url = url,
                name = name,
                emoticonPackId = emoticonPackId,
            )
        }
    }
    fun toEmoticon(): EmoticonInfo {
        return EmoticonInfo.of(
            emoticonId,
            name,
            url,
            emoticonPackId,
        )
    }
}
