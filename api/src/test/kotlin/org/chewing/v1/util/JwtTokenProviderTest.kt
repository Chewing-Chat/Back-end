package org.chewing.v1.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.AuthorizationException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.auth.JwtToken
import org.chewing.v1.util.security.JwtTokenUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Date
import javax.crypto.SecretKey

class JwtTokenProviderTest {

    private lateinit var jwtTokenProvider: JwtTokenUtil

    private lateinit var secretKeyString: String

    private var accessExpiration: Long = 0

    private var refreshExpiration: Long = 0

    private lateinit var secretKey: SecretKey

    @BeforeEach
    fun setup() {
        secretKeyString =
            "mysecretkey12345asdfvasdfvhjaaaaaaaaaaaaaaaaaaaaaaaaaslfdjasdlkr231243123412"
        accessExpiration = 1000L * 60 * 15
        refreshExpiration = 1000L * 60 * 60 * 24 * 30

        secretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray())
        jwtTokenProvider = JwtTokenUtil(secretKeyString, accessExpiration, refreshExpiration)
    }

    @Test
    @DisplayName("JWT 토큰 생성 테스트")
    fun `test createJwtToken`() {
        val userId = TestDataFactory.createUserId()
        val jwtToken: JwtToken = jwtTokenProvider.createJwtToken(userId)

        // Validate the token structure
        assert(jwtToken.accessToken.isNotEmpty())
        assert(jwtToken.refreshToken.token.isNotEmpty())
    }

    @Test
    @DisplayName("유효한 토큰에 대해 검증이 성공하는지 테스트`")
    fun `test validateToken with valid token`() {
        val userId = TestDataFactory.createUserId()
        val token = jwtTokenProvider.createAccessToken(userId)

        // Should not throw exception
        jwtTokenProvider.validateToken(token)
    }

    @Test
    @DisplayName("만료된 토큰에 대해 검증이 실패하는지 테스트")
    fun `test validateToken with expired token`() {
        val userId = "testUser"
        val claims: Claims = Jwts.claims().setSubject(userId)
        val now = Date()
        val expiryDate = Date(now.time - 1000) // 1 second ago
        val token = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()

        val exception = assertThrows<AuthorizationException> {
            jwtTokenProvider.validateToken(token)
        }

        assert(exception.errorCode == ErrorCode.TOKEN_EXPIRED)
    }

    @Test
    @DisplayName("유효하지 않은 토큰에 대해 검증이 실패하는지 테스트")
    fun `test validateToken with invalid token`() {
        val exception = assertThrows<AuthorizationException> {
            jwtTokenProvider.validateToken("invalid.token")
        }
        assert(exception.errorCode == ErrorCode.INVALID_TOKEN)
    }

    @Test
    @DisplayName("토큰에서 사용자 ID를 올바르게 추출하는지 테스트")
    fun `test getUserIdFromToken`() {
        val userId = TestDataFactory.createUserId()
        val token = jwtTokenProvider.createAccessToken(userId)
        val extractedUserId = jwtTokenProvider.getUserIdFromToken(token)
        assert(extractedUserId == userId)
    }

    @Test
    @DisplayName("토큰을 올바르게 정리하는지 테스트")
    fun `test cleanedToken`() {
        val token = "Bearer someToken"
        val cleaned = jwtTokenProvider.cleanedToken(token)
        assert(cleaned == "someToken")
    }

    @Test
    @DisplayName("Refresh 토큰으로 새로운 JwtToken을 재발급할 수 있어야 한다")
    fun `test refresh`() {
        val userId = TestDataFactory.createUserId()
        val jwtToken = jwtTokenProvider.createJwtToken(userId)
        val refreshToken = jwtToken.refreshToken.token

        val (newToken, extractedUserId) = jwtTokenProvider.refresh("Bearer $refreshToken")

        assert(newToken.accessToken.isNotBlank())
        assert(newToken.refreshToken.token.isNotBlank())
        assert(extractedUserId == userId)
    }

    @Test
    @DisplayName("정상적인 refresh token에 대해 validateRefreshToken이 성공하는지 테스트")
    fun `test validateRefreshToken with valid token`() {
        val userId = TestDataFactory.createUserId()
        val refreshToken = jwtTokenProvider.createRefreshToken(userId).token
        jwtTokenProvider.validateRefreshToken("Bearer $refreshToken")
    }

    @Test
    @DisplayName("Bearer prefix가 있는 토큰에서도 사용자 ID를 추출할 수 있어야 한다")
    fun `test getUserIdFromToken with Bearer token`() {
        val userId = TestDataFactory.createUserId()
        val token = jwtTokenProvider.createAccessToken(userId)
        val bearerToken = "Bearer $token"

        val extractedUserId = jwtTokenProvider.getUserIdFromToken(bearerToken)
        assert(extractedUserId == userId)
    }
}
