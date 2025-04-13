package org.chewing.v1.util.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.chewing.v1.error.AuthorizationException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.auth.JwtToken
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.UserId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenUtil(
    @Value("\${jwt.secret}") private val secretKeyString: String,
    @Value("\${jwt.access-expiration}") private val accessExpiration: Long,
    @Value("\${jwt.refresh-expiration}") private val refreshExpiration: Long,
) {
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray())

    fun createJwtToken(userId: UserId): JwtToken {
        val accessToken = createAccessToken(userId)
        val refreshToken = createRefreshToken(userId)
        return JwtToken.of(accessToken, refreshToken)
    }

    // JWT Access Token 생성
    fun createAccessToken(userId: UserId): String {
        val claims: Claims = Jwts.claims().setSubject(userId.id)
        val now = Date()
        val expiryDate = Date(now.time + accessExpiration)
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }

    // JWT Refresh Token 생성
    fun createRefreshToken(userId: UserId): RefreshToken {
        val claims: Claims = Jwts.claims().setSubject(userId.id)
        val now = Date()
        val expiryDate = Date(now.time + refreshExpiration)
        val token = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
        return RefreshToken.of(token, LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()))
    }

    // 토큰 유효성 검사 수정함(예외 발생 방식으로 수정)
    fun validateToken(token: String) {
        try {
            getClaimsFromToken(token)
        } catch (e: ExpiredJwtException) {
            throw AuthorizationException(ErrorCode.TOKEN_EXPIRED) // 엑세스 토큰 만료 예외 발생
        } catch (e: JwtException) {
            throw AuthorizationException(ErrorCode.INVALID_TOKEN) // JWT 관련 일반 예외 발생
        }
    }

    // 리프레시 토큰 유효성 검사 추가
    fun validateRefreshToken(refreshToken: String) {
        val cleanedToken = refreshToken.removePrefix("Bearer ").trim()
        validateToken(cleanedToken) // 동일한 유효성 검사 메서드 사용
    }

    // 토큰에서 사용자 ID 추출
    fun getUserIdFromToken(token: String): UserId {
        val claims = getClaimsFromToken(cleanedToken(token))
        return UserId.of(claims.subject)
    }

    // 토큰에서 클레임(사용자 관련 정보(예: 사용자 ID, 권한 등)) 추출
    private fun getClaimsFromToken(token: String): Claims = Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .body

    // token 글자 깔끔히 정리
    fun cleanedToken(token: String): String = token.removePrefix("Bearer ").trim()

    fun refresh(token: String): Pair<JwtToken, UserId> {
        val cleanedToken = cleanedToken(token)
        validateRefreshToken(cleanedToken)
        val userId = getUserIdFromToken(cleanedToken)
        return Pair(createJwtToken(userId), userId)
    }
}
