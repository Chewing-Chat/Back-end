package org.chewing.v1.model.contact

class PhoneNumber private constructor(
    val e164PhoneNumber: String,
) : Contact() {
    companion object {
        fun of(
            phoneNumber: String,
        ): PhoneNumber {
            return PhoneNumber(
                e164PhoneNumber = phoneNumber,
            )
        }
    }
}
