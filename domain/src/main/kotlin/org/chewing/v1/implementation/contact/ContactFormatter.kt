package org.chewing.v1.implementation.contact

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.contact.LocalPhoneNumber
import org.chewing.v1.model.contact.PhoneNumber
import org.springframework.stereotype.Component

@Component
class ContactFormatter(
    private val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance(),
) {
    fun formatContact(localPhoneNumber: LocalPhoneNumber): PhoneNumber {
        try {
            val internationalNumber = "+${localPhoneNumber.countryCode}${localPhoneNumber.number}"
            val parsedNumber = phoneUtil.parse(internationalNumber, null)
            if (!phoneUtil.isValidNumber(parsedNumber)) {
                throw ConflictException(ErrorCode.INVALID_PHONE_NUMBER)
            }
            val formattedPhoneNumber = phoneUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
            return PhoneNumber.of(formattedPhoneNumber)
        } catch (e: NumberParseException) {
            throw ConflictException(ErrorCode.INVALID_PHONE_NUMBER)
        }
    }

    fun extractCountryCodeAndLocalNumber(e164PhoneNumber: PhoneNumber): LocalPhoneNumber {
        return try {
            val parsedNumber = phoneUtil.parse(e164PhoneNumber.e164PhoneNumber, null)
            val countryCode = "+${parsedNumber.countryCode}"
            val localNumber = parsedNumber.nationalNumber.toString()
            LocalPhoneNumber.of(localNumber, countryCode)
        } catch (e: NumberParseException) {
            throw ConflictException(ErrorCode.INVALID_PHONE_NUMBER)
        }
    }
}
