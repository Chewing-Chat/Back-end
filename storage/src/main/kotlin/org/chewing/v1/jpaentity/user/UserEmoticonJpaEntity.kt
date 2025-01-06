package org.chewing.v1.jpaentity.user

import jakarta.persistence.*
import org.chewing.v1.model.user.UserEmoticonPackInfo
import java.time.LocalDateTime

@Entity
@Table(name = "user_emoticon", schema = "chewing")
internal class UserEmoticonJpaEntity(
    @EmbeddedId
    private val id: UserEmoticonId,
    private val createAt: LocalDateTime,
) {
    fun toUserEmoticon(): UserEmoticonPackInfo {
        return UserEmoticonPackInfo.of(
            userId = id.userId,
            emoticonPackId = id.emoticonPackId,
            createAt = createAt,
        )
    }
}
