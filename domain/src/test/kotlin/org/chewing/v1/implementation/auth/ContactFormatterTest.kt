package org.chewing.v1.implementation.auth

import org.chewing.v1.TestDataFactory
import org.chewing.v1.implementation.contact.ContactFormatter
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.platform.commons.annotation.Testable

@DisplayName("ContactFormatter 테스트")
class ContactFormatterTest {
    private val contactFormatter = ContactFormatter()

    @Test
    fun `전화번호 포맷팅`() {
        val phoneNumber = TestDataFactory.createLocalPhoneNumber()
        val formattedPhoneNumber = contactFormatter.formatContact(phoneNumber)
        assert(formattedPhoneNumber.e164PhoneNumber == "+821012345678")
    }

    @Test
    fun `전화번호 추출`() {
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val localPhoneNumber = contactFormatter.extractCountryCodeAndLocalNumber(phoneNumber)
        assert(localPhoneNumber.countryCode == "82")
        assert(localPhoneNumber.number == "01012345678")
    }
}
