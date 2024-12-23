package org.chewing.v1.model.user

class UserName private constructor(
    val firstName: String,
    val lastName: String,
) {
    fun firstName(): String {
        return firstName
    }

    fun lastName(): String {
        return lastName
    }

    companion object {
        fun of(firstName: String, lastName: String): UserName {
            return UserName(firstName, lastName)
        }
    }
}
