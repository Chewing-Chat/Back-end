package org.chewing.v1.model.auth

class PhoneNumber(
    val countryCode: String,
    val number: String,
) : Credential() {
    companion object {
        fun of(
            countryCode: String,
            number: String,
        ): PhoneNumber {
            return PhoneNumber(
                countryCode = countryCode,
                number = number,
            )
        }
    }
}
