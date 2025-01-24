package org.chewing.v1.model.contact

class LocalPhoneNumber private constructor(
    val number: String,
    val countryCode: String,
) {
    companion object {
        fun of(
            number: String,
            countryCode: String,
        ): LocalPhoneNumber {
            return LocalPhoneNumber(
                number = number,
                countryCode = countryCode,
            )
        }
    }
}
