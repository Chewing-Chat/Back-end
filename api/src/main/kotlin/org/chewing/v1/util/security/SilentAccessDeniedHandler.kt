package org.chewing.v1.util.security

import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class SilentAccessDeniedHandler : AccessDeniedHandler {
    private val logger = KotlinLogging.logger {}

    @Throws(IOException::class, ServletException::class)
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        logger.warn("Access denied on ${request.requestURI}: ${accessDeniedException.message}")

        if (!response.isCommitted) {
            response.status = HttpServletResponse.SC_OK
            response.writer.write("")
            response.writer.flush()
        }
    }
}
