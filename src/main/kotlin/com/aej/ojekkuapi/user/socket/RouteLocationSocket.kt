package com.aej.ojekkuapi.user.socket

import com.aej.ojekkuapi.user.repository.UserRepository
import com.corundumstudio.socketio.BroadcastAckCallback
import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.DataListener
import org.springframework.stereotype.Service

@Service
class RouteLocationSocket {

    private lateinit var server: SocketIOServer

    fun init(userRepository: UserRepository) {
        val config = Configuration()
        config.hostname = "localhost"
        config.port = 9092

        server = SocketIOServer(config)
        server.addConnectListener {
        }

        server.addEventListener("route_request", UserRequest::class.java, DataListener { client, data, ackSender ->
            client.sendEvent("route_callback", "datanya niiiiiii -")
        })

        val users = userRepository.getAllUser().getOrNull().orEmpty()
        users.map { it.username }.forEach { username ->
            server.addEventListener(username, String::class.java, DataListener { client, data, ackSender ->
                println("connect to event -> $username")
            })
        }

        server.start()
    }

    fun send(eventName: String, data: String) {
        println("asuuuu send event -> $eventName")
        server.broadcastOperations.sendEvent(eventName, data, object : BroadcastAckCallback<String>(String::class.java) {
            override fun onAllSuccess() {
                super.onAllSuccess()
                println("asuuuu send success")
            }

            override fun onClientTimeout(client: SocketIOClient?) {
                super.onClientTimeout(client)
                println("asuuuuu send failure -> ${client?.sessionId}")
            }
        })
    }
}