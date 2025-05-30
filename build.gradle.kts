import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    //kapt 1.9 오류 무시해도됨
    kotlin("jvm") version "2.0.0"
    kotlin("kapt") version "2.0.0"
    kotlin("plugin.spring") apply false
    kotlin("plugin.jpa") apply false
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.asciidoctor.jvm.convert")
    id("org.jlleitschuh.gradle.ktlint")
    jacoco
    id("jacoco-report-aggregation")
}

java.sourceCompatibility = JavaVersion.valueOf("VERSION_${property("javaVersion")}")

dependencies {
    jacocoAggregation(project(":common"))
    jacocoAggregation(project(":api"))
    jacocoAggregation(project(":domain"))
}

allprojects {
    group = "${property("projectGroup")}"
    version = "${property("applicationVersion")}"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.asciidoctor.jvm.convert")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "jacoco")
    apply(plugin = "org.jetbrains.kotlin.kapt")

    jacoco {
        toolVersion = "0.8.12"
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("io.github.microutils:kotlin-logging:3.0.5")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
        kapt("org.springframework.boot:spring-boot-configuration-processor")
        //
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("com.ninja-squad:springmockk:${property("springMockkVersion")}")
        //
        implementation("org.springframework.boot:spring-boot-starter-websocket")
        // 코루틴
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${property("kotlinxCoroutinesVersion")}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${property("kotlinxCoroutinesVersion")}")
        testImplementation("org.jetbrains.kotlinx", "kotlinx-coroutines-test", "${property("kotlinxCoroutinesVersion")}")

        //env
        implementation("me.paulschwarz:spring-dotenv:4.0.0")
        implementation("org.springframework.boot:spring-boot-starter-web")
    }

    tasks {
        build {
            dependsOn(ktlintFormat)
        }
        test {
            useJUnitPlatform()
            maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
            forkEvery = 100
        }
        bootJar {
            enabled = false
        }
        jar {
            enabled = true
        }
    }

    java.sourceCompatibility = JavaVersion.valueOf("VERSION_${property("javaVersion")}")

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xjsr305=strict",
                "-progressive",
                "-opt-in=kotlin.RequiresOptIn",
                "-Xjvm-default=all",
                "-Xinline-classes",
            )
            jvmTarget.set(JvmTarget.valueOf("JVM_${project.property("javaVersion")}"))
        }
    }
}
tasks {

    test {
        finalizedBy(testCodeCoverageReport)
    }

    testCodeCoverageReport {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it) {
                        exclude(
                            "**/ChewingApplicationKt.class",
                            "**/ChewingApplicationKt\$*.class",
//                            "**/model/**",
//                            "**/dto/**",
                        )
                    }
                },
            ),
        )
    }

    bootJar {
        enabled = false
    }
    jar {
        enabled = false
    }
}
