package com.aej.ojekkuapi.authentication

import com.aej.ojekkuapi.*
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.user.services.UserServices
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.NoHandlerFoundException
import java.net.NoRouteToHostException
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
            val headerToken = request.getHeader(JwtConfig.HEADER_KEY_AUTHORIZATION)

            if (headerToken.isNullOrEmpty() && JwtConfig.isPermit(request)) {
                filterChain.doFilter(request, response)
            } else {
                val claims = validate(request)
                if (claims[Constant.CLAIMS] != null) {
                    setupAuthentication(claims, request) {
                        filterChain.doFilter(request, response)
                    }
                } else {
                    SecurityContextHolder.clearContext()
                    throw OjekuException("token required")
                }
            }
        } catch (e: Exception) {
            val errorResponse = BaseResponse<Empty>()
            e.printStackTrace()

            val message = when (e) {
                is UnsupportedJwtException -> "Error unsupported!"
                is MalformedJwtException, is SignatureException -> "Token invalid!"
                else -> e.message ?: "Failure"
            }

            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            errorResponse.status = false
            errorResponse.message = message

            val responseString = ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(errorResponse)

            response.writer.println(responseString)
        }
    }

    private fun validate(request: HttpServletRequest): Claims {
        val jwtToken = request.getHeader(JwtConfig.HEADER_KEY_AUTHORIZATION)
        if (jwtToken.isNullOrEmpty() and !JwtConfig.isPermit(request)) {
            throw OjekuException("Token empty!")
        }
        return Jwts.parserBuilder()
            .setSigningKey(Constant.SECRET.toByteArray())
            .build()
            .parseClaimsJws(jwtToken)
            .body
    }

    private fun setupAuthentication(claims: Claims, request: HttpServletRequest, doOnNext: () -> Unit) {
        val headerCoordinate = request.getHeader(JwtConfig.HEADER_KEY_COORDINATE)
        if (headerCoordinate.isNullOrEmpty()) {
            throw OjekuException("Header field `${JwtConfig.HEADER_KEY_COORDINATE}` is empty!")
        }

        val authorities = claims[Constant.CLAIMS] as List<String>
        val authStream = authorities.stream().map { SimpleGrantedAuthority(it) }
            .collect(Collectors.toList())

        val auth = UsernamePasswordAuthenticationToken(claims.subject, null, authStream)
        SecurityContextHolder.getContext().authentication = auth
        val userId = SecurityContextHolder.getContext().authentication.principal as? String

        val latLngString = headerCoordinate
            .replace(" ", "")
            .split(",")

        if (latLngString.size != 2) {
            throw OjekuException("Header field `${JwtConfig.HEADER_KEY_COORDINATE}` invalid!")
        }
        val userCoordinate = Coordinate(
            latitude = latLngString[0].toDoubleOrNull() ?: 0.0,
            longitude = latLngString[1].toDoubleOrNull() ?: 0.0
        )

        if (userId.isNullOrEmpty()) {
            throw OjekuException("User invalid!")
        }

        userServices.updateCoordinate(userId, userCoordinate)
        doOnNext.invoke()
    }
}