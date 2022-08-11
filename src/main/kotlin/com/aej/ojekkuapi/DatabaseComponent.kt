package com.aej.ojekkuapi

import com.mongodb.client.MongoClient
import org.litote.kmongo.KMongo
import org.springframework.stereotype.Component

@Component
class DatabaseComponent {

    companion object {
        private const val DATABASE_URL = "mongodb+srv://aej:1234@cluster0.npggr.mongodb.net/?retryWrites=true&w=majority&serverSelectionTimeoutMS=10000&connectTimeoutMS=5000"
        const val DATABASE_NAME = "ojeku"
    }

    final val database: MongoClient = KMongo.createClient(DATABASE_URL)
}