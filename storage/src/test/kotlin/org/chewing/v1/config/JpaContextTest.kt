package org.chewing.v1.config

import org.chewing.v1.DbTestApplication
import org.chewing.v1.repository.support.JpaDataGenerator
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@DataJpaTest
@ContextConfiguration(classes = [DbTestApplication::class])
@ActiveProfiles("test")
@Import(CoreDataSourceConfig::class, JpaDataGenerator::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ComponentScan(basePackages = ["org.chewing.v1.repository.jpa"])
abstract class JpaContextTest
