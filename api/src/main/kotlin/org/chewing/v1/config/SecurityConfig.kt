package org.chewing.v1.config

import org.chewing.v1.util.security.JwtAuthenticationEntryPoint
import org.chewing.v1.util.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val entryPoint: JwtAuthenticationEntryPoint,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/api/auth/phone/create/**",
                        "/api/auth/email/create/**",
                        "/api/auth/refresh",
                        "/api/auth/logout",
                        "/ws-stomp/**",
                        "/bot/chat",
                        "/api/tts",
                        "/api/openvidu/**",
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java) // JWT 인증 필터 추가
            .exceptionHandling { it.authenticationEntryPoint(entryPoint) }

        return http.build()
    }
}
