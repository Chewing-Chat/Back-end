package org.chewing.v1.util.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.chewing.v1.error.AuthorizationException
import org.chewing.v1.error.ErrorCode
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@Component
class JwtAuthenticationFilter(
    private val jwtTokenUtil: JwtTokenUtil,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // 필터링 제외 대상은 바로 다음 필터로 전달
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response)
            return
        }

        // 여기부터 수정
        try {
            val token = resolveToken(request)
            jwtTokenUtil.validateToken(token) // 토큰 유효성 검사 및 예외 처리
            val userId = jwtTokenUtil.getUserIdFromToken(token)
            val authentication = UsernamePasswordAuthenticationToken(userId, null, emptyList())
            SecurityContextHolder.getContext().authentication = authentication

            request.setAttribute("userId", userId)
        } catch (e: AuthorizationException) {
            request.setAttribute("Exception", e)
        }
        filterChain.doFilter(request, response)
    }

    @Throws(ServletException::class)
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path.startsWith("/api/auth/create/send") ||
                path.startsWith("/api/auth/create/verify") ||
                path.startsWith("/api/auth/refresh") ||
                path.startsWith("/api/auth/reset/send") ||
                path.startsWith("/api/auth/reset/verify") ||
                path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/logout") ||
                path.startsWith("/api/hello") ||
                path.startsWith("/docs")
//                path.startsWith("/api/announcement/list") ||
//                path.matches(Regex("/api/announcement/\\d+")) // ✅ 숫자로 된 ID 처리
    }

    private fun resolveToken(request: HttpServletRequest): String {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            jwtTokenUtil.cleanedToken(bearerToken)
        } else {
            throw AuthorizationException(ErrorCode.INVALID_TOKEN)
        }
    }
}


