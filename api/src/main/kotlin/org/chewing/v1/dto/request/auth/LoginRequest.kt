package org.chewing.v1.dto.request.auth

import org.chewing.v1.model.auth.PhoneNumber

data class LoginRequest(
    val phoneNumber: String = "",
    val countryCode: String = "",
    val password: String = "",
) {
    fun toPhoneNumber(): PhoneNumber = PhoneNumber.of(countryCode, phoneNumber)
    fun toPassword(): String = password
}
