package com.aej.ojekkuapi.utils.extensions

import com.aej.ojekkuapi.DatabaseComponent
import com.aej.ojekkuapi.user.entity.extra.DriverExtras
import com.mongodb.client.MongoCollection
import org.litote.kmongo.util.KMongoUtil

fun Any.asDriverExtras(): DriverExtras = this as DriverExtras

inline fun <reified T : Any> DatabaseComponent.collection(): MongoCollection<T> =
    database
        .getDatabase(DatabaseComponent.DATABASE_NAME)
        .getCollection(KMongoUtil.defaultCollectionName(T::class), T::class.java)
