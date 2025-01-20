package org.chewing.v1.model.user

@JvmInline
value class UserId private constructor(
    val id: String,
){
    companion object {
        fun of(
            id: String,
        ): UserId {
            return UserId(id)
        }
    }
}
