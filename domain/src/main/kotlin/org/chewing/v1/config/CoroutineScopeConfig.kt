package org.chewing.v1.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.chewing.v1.util.DefaultScope
import org.chewing.v1.util.IoScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoroutineScopeConfig {
    @IoScope
    @Bean
    fun ioCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @DefaultScope
    @Bean
    fun defaultScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.Default + SupervisorJob())
    }
}
