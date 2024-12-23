package org.chewing.v1.jpaentity.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class FeedLikeId(
    @Column(name = "user_id")
    val userId: String,

    @Column(name = "feed_id")
    val feedId: String,
) : Serializable
