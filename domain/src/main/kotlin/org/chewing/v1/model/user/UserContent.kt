package org.chewing.v1.model.user

class UserContent private constructor(
    val name: UserName,
    val birth: String,
) {
    companion object {
        fun of(
            firstName: String,
            lastName: String,
            birth: String,
        ): UserContent {
            return UserContent(
                name = UserName.of(firstName, lastName),
                birth = birth,
            )
        }
    }
}
