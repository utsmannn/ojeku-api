package com.aej.ojekkuapi

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.Exception

@ControllerAdvice
class OjekuExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [OjekuException::class])
    fun handleThrowable(throwable: OjekuException): ResponseEntity<BaseResponse<Empty>> {
        return ResponseEntity(BaseResponse.failure(throwable.message ?: "Failure"), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        ex.printStackTrace()
        return ResponseEntity(BaseResponse.failure<BaseResponse<Empty>>(ex.message ?: "Failure"), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}