package org.chewing.v1.jpaentity.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.chewing.v1.model.user.UserId
import java.io.Serializable

@Embeddable
data class UserEmoticonId(
    @Column(name = "user_id")
    val userId: String,

    @Column(name = "emoticon_pack_id")
    val emoticonPackId: String,
) : Serializable {
    companion object {
        fun of(userId: UserId, emoticonPackId: String): UserEmoticonId {
            return UserEmoticonId(userId.id, emoticonPackId)
        }
    }
}
