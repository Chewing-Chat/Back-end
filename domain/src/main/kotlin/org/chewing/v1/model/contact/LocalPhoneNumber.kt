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

    // 추가된 toString() 메서드
    override fun toString(): String {
        return when {
            number.startsWith("+") -> number // 이미 국제 형식이면 그대로 반환
            number.startsWith(countryCode) -> number // ✅ 이미 국가 코드 포함된 경우 그대로 사용
            number.startsWith("0") -> "+$countryCode${number.removePrefix("0")}" // 010 -> +8210 변환
            else -> "+$countryCode$number" // 기본적으로 국가 코드 붙이기
        }
    }
}
