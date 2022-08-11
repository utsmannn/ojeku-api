package com.aej.ojekkuapi.utils.fcm

data class FirebaseMessage(
    var type: FirebaseMessageType = FirebaseMessageType.MESSAGE,
    var data: Any
) {
    companion object {
        fun createTransaction(data: Any): FirebaseMessage {
            return FirebaseMessage(
                type = FirebaseMessageType.TRANSACTION,
                data = data
            )
        }
    }
}