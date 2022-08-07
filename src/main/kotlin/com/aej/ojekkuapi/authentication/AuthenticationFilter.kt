package com.aej.ojekkuapi.authentication

import com.aej.ojekkuapi.BaseResponse
import com.aej.ojekkuapi.Constant
import com.aej.ojekkuapi.Empty
import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.user.services.UserServices
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.UnsupportedJwtException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticationFilter : OncePerRequestFilter() {

    @Autowired
    private lateinit var userServices: UserServices
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val claims = validate(request)
            when {
                JwtConfig.isPermit(request) -> {
                    filterChain.doFilter(request, response)
                }
                claims[Constant.CLAIMS] != null -> {
                    setupAuthentication(claims) {
                        filterChain.doFilter(request, response)
                    }
                }
                else -> {
                    SecurityContextHolder.clearContext()
                    throw OjekuException("token required")
                }
            }
        } catch (e: Exception) {
            val errorResponse = BaseResponse<Empty>()
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            e.printStackTrace()

            when (e) {
                is UnsupportedJwtException -> {
                    errorResponse.message = "error unsupported!"
                    val responseString = ObjectMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(errorResponse)

                    response.writer.println(responseString)
                }
                else -> {
                    errorResponse.message = e.message ?: "token invalid!"
                    val responseString = ObjectMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(errorResponse)
                    response.writer.println(responseString)
                }
            }
        } catch (e: OjekuException) {
            val errorResponse = BaseResponse<Empty>()
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            e.printStackTrace()

            errorResponse.message = e.message ?: "Unknown error!"
            val responseString = ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(errorResponse)

            response.writer.println(responseString)
        }

    }

    private fun validate(request: HttpServletRequest): Claims {
        val jwtToken = request.getHeader("Authorization")
        if (jwtToken.isNullOrEmpty()) {
            throw OjekuException("Error!")
        } else {
            return Jwts.parserBuilder()
                .setSigningKey(Constant.SECRET.toByteArray())
                .build()
                .parseClaimsJws(jwtToken)
                .body
        }
    }

    private fun setupAuthentication(claims: Claims, doOnNext: () -> Unit) {
        val authorities = claims[Constant.CLAIMS] as List<String>
        val authStream = authorities.stream().map { SimpleGrantedAuthority(it) }
            .collect(Collectors.toList())

        val auth = UsernamePasswordAuthenticationToken(claims.subject, null, authStream)
        SecurityContextHolder.getContext().authentication = auth
        val userId = SecurityContextHolder.getContext().authentication.principal as? String
        doOnNext.invoke()
    }
}