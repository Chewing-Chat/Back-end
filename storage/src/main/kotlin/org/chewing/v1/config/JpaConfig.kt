package org.chewing.v1.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["org.chewing.v1.jparepository", "org.chewing.v1.jpaentity"])
class JpaConfig
