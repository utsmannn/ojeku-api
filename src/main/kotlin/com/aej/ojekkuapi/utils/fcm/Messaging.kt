package com.aej.ojekkuapi.utils.fcm

import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.utils.extensions.toResult
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage

object Messaging {
    fun init() {
        val configStream = FirebaseUtils.getFcmConfigStream() ?: throw OjekuException("Config stream error!")
        val credential = GoogleCredentials.fromStream(configStream)
        val option = FirebaseOptions.builder()
            .setCredentials(credential)
            .build()

        FirebaseApp.initializeApp(option)
    }

    fun broadcastMessage(message: FirebaseMessage, vararg token: String): Result<Boolean> {
        if (token.isEmpty()) {
            throw OjekuException("Driver not found!")
        }
        val messageJson = ObjectMapper().writeValueAsString(message.data)
        val multicastMessage = MulticastMessage.builder()
            .addAllTokens(token.toList())
            .putData(message.type.name, messageJson)
            .build()

        val response = FirebaseMessaging.getInstance().sendMulticast(multicastMessage)
            .failureCount == 0
        return response.toResult()
    }
}