package com.aej.ojekkuapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PreDestroy
import kotlin.jvm.Throws

@SpringBootApplication
class OjekkuApiApplication

fun main(args: Array<String>) {
    runApplication<OjekkuApiApplication>(*args)
}

class TerminateBean {
    @PreDestroy
    @Throws(Exception::class)
    fun onShutdown() {
        println("Asuuuuuuuuu on shutdown")
    }
}

@Configuration
class ShutdownConfig {

    @Bean
    fun getTerminateBean(): TerminateBean {
        return TerminateBean()
    }
}