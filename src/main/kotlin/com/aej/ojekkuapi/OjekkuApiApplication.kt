package com.aej.ojekkuapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.FrameworkServlet
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer

@SpringBootApplication
class OjekkuApiApplication

fun main(args: Array<String>) {
    runApplication<OjekkuApiApplication>(*args)
}
