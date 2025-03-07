dependencies {
    implementation(project(":common"))
    compileOnly("org.springframework:spring-context")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.googlecode.libphonenumber:libphonenumber:8.13.18")
}
