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

            // ✅ 원래 전화번호 출력 (디버깅용)
            println("📢 원래 전화번호: ${localPhoneNumber.number}")

            var cleanedNumber = localPhoneNumber.number

            // ✅ `+` 중복 방지: 이미 `+`가 붙어있으면 그대로 사용
            if (cleanedNumber.startsWith("+")) {
                println("📢 이미 국제 번호 형식임: $cleanedNumber")
            } else if (cleanedNumber.startsWith("0")) {
                // ✅ 010 -> +8210 변환
                cleanedNumber = "+${localPhoneNumber.countryCode}${cleanedNumber.removePrefix("0")}"
            } else {
                // ✅ 기본적으로 국가 코드 추가
                cleanedNumber = "+${localPhoneNumber.countryCode}$cleanedNumber"
            }

            println("📢 국가코드 확인 후 전화번호: $cleanedNumber") // ✅ 변환된 값 출력


            val parsedNumber = phoneUtil.parse(cleanedNumber, null)
            if (!phoneUtil.isValidNumber(parsedNumber)) {
                println("❌ 유효하지 않은 번호: $cleanedNumber") // ✅ 유효성 검사 실패 로그
                throw ConflictException(ErrorCode.INVALID_PHONE_NUMBER)
            }
            val formattedPhoneNumber = phoneUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
            println("📢 변환 후 전화번호 (E.164): $formattedPhoneNumber") // 🚨 로그 추가

            return PhoneNumber.of(formattedPhoneNumber)
        } catch (e: NumberParseException) {
            println("❌ NumberParseException 발생: ${e.message}") // 🚨 로그 추가
            throw ConflictException(ErrorCode.INVALID_PHONE_NUMBER)
        }
    }

    fun extractCountryCodeAndLocalNumber(e164PhoneNumber: PhoneNumber): LocalPhoneNumber {
        return try {
            val parsedNumber = phoneUtil.parse(e164PhoneNumber.e164PhoneNumber, null)
            if (!phoneUtil.isValidNumber(parsedNumber)) {
                throw ConflictException(ErrorCode.INVALID_PHONE_NUMBER)
            }

            val countryCode = parsedNumber.countryCode.toString()

            val nationalFormatted = phoneUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
            val localNumber = nationalFormatted.replace(Regex("\\D"), "")

            LocalPhoneNumber.of(localNumber, countryCode)
        } catch (e: NumberParseException) {
            throw ConflictException(ErrorCode.INVALID_PHONE_NUMBER)
        }
    }
}
