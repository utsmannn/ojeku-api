package com.aej.ojekkuapi.messaging

import com.aej.ojekkuapi.messaging.entity.FcmMessage
import com.aej.ojekkuapi.utils.BaseComponent
import com.aej.ojekkuapi.utils.toJson
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.litote.kmongo.json
import org.springframework.stereotype.Component

@Component
class MessagingComponent : BaseComponent() {

    suspend fun sendMessage(token: String, fcmMessageData: FcmMessage.FcmMessageData): Result<Any> {
        val fcmMessage = FcmMessage(token, fcmMessageData)
        val requestBody = fcmMessage.toJson().toRequestBody("application/json; charset=utf-8".toMediaType())
        println("send message -> $token | ${fcmMessageData.bookingId}")

        val url = "https://fcm.googleapis.com/fcm/send"
        val headers = mapOf(
            "Authorization" to "key=$SERVER_KEY",
            "Content-Type" to "application/json"
        ).toHeaders()

        println("message body -> ${fcmMessage.toJson()}")
        return postHttp(
            url = url,
            header = headers,
            requestBody = requestBody
        )
    }

    companion object {
        private const val SERVER_KEY =
            "AAAA6_zvnHI:APA91bF-VGB3iJWSxPycEOKxzEHKsXDjGABC0uL_37lQKQcRKolPnehjQETDDNpHbCk-Rb6TtfG2NR7PTgt0ku7ZhqpRdMsNKnGyEXHxWkI-uKE4rBc4YTQ_sSs84_uv-2yQHFVi6Qpa"
    }
}