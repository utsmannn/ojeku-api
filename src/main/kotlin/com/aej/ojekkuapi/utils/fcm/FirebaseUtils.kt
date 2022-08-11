package com.aej.ojekkuapi.utils.fcm

import java.io.InputStream

object FirebaseUtils {
    fun getFcmConfigStream(): InputStream? {
        return this::class.java.classLoader.getResourceAsStream("firebase-admin.json")
    }
}