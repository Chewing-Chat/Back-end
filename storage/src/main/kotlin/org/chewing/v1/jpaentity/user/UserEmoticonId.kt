package org.chewing.v1.jpaentity.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class UserEmoticonId(
    @Column(name = "user_id")
    val userId: String,

    @Column(name = "emoticon_pack_id")
    val emoticonPackId: String,
) : Serializable
