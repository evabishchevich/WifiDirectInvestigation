package org.thaliproject.p2p.wifidirectdemo.service.messaging

interface MessagingService {

    fun startMessagingServer(isGroupOwner: Boolean)

    fun sendMessageTo(client: String, message: Message)

    fun sendToAll(message: Message)
}