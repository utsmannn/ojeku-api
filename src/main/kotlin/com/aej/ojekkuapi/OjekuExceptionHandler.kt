package com.aej.ojekkuapi

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.Exception

@ControllerAdvice
class OjekuExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [OjekuException::class])
    fun handleThrowable(throwable: OjekuException): ResponseEntity<BaseResponse<Empty>> {
        println("haduh -> ${throwable.message}")
        return ResponseEntity(BaseResponse.failure(throwable.message ?: "Failure"), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    // // 404
    //    @ExceptionHandler({ NoHandlerFoundException.class })
    //    @ResponseBody
    //    @ResponseStatus(HttpStatus.NOT_FOUND)
    //    public CustomResponse notFound(final NoHandlerFoundException ex) {
    //        return new CustomResponse(HttpStatus.NOT_FOUND.value(), "page not found");
    //    }

    /*@ExceptionHandler(value = [NoHandlerFoundException::class])
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(throwable: NoHandlerFoundException): ResponseEntity<BaseResponse<Empty>> {
        return ResponseEntity(BaseResponse.failure(throwable.message ?: "No routes"), HttpStatus.NOT_FOUND)
    }*/

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity(BaseResponse.failure<Empty>(ex.message ?: "Failure"), status)
    }

    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        println("asuuuuuuu")
        return ResponseEntity(BaseResponse.failure<Empty>(ex.message ?: "Failure"), status)
    }

    /*@ResponseStatus(HttpStatus.NOT_FOUND)
    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        println("asuuuuu")
        return ResponseEntity(BaseResponse.failure<Empty>(ex.message ?: "Failure"), HttpStatus.NOT_FOUND)
    }*/

}