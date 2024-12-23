plugins {
    id("java-test-fixtures")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
    implementation(project(":common"))
    compileOnly(project(":domain"))
    runtimeOnly("com.mysql:mysql-connector-j")
    testFixturesRuntimeOnly("com.h2database:h2")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    testFixturesRuntimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring30x:4.11.0")
    testImplementation(project(":domain"))
}
