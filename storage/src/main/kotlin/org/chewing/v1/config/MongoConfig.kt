package org.chewing.v1.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["org.chewing.v1.mongorepository", "org.chewing.v1.mongoentity"])
class MongoConfig
