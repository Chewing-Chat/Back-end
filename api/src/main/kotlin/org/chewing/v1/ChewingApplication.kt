package org.chewing.v1

import org.chewing.v1.util.Generated
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
@Generated
@SpringBootApplication(scanBasePackages = ["org.chewing.v1"])
class ChewingApplication
fun main(args: Array<String>) {
    runApplication<ChewingApplication>(*args)
}
