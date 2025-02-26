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
                        "/api/auth/create/send",
                        "/api/auth/create/verify",
                        "/api/auth/reset/send",
                        "/api/auth/refresh",
                        "/api/auth/login",
                        "/api/auth/logout",
                        "/api/auth/reset/verify",
                        "/ws-stomp/**",
                        "/api/hello",
                        "/docs/**",
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling { it.authenticationEntryPoint(entryPoint) }

        return http.build()
    }
}
