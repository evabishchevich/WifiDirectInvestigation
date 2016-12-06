package org.thaliproject.p2p.wifidirectdemo.service.messaging

import android.content.Context
import android.net.wifi.WifiManager
import org.thaliproject.p2p.wifidirectdemo.WifiDirectInfo

class MessagingServiceImpl(ctx: Context, wifiManager: WifiManager, wifiDirectInfo: WifiDirectInfo) : MessagingService {

    private val ctx: Context
    private val wifiManager: WifiManager
    private val wifiDirectInfo: WifiDirectInfo
    private lateinit var messagingServer: MessagingServer
    private var messagingServerStarted = false;

    init {
        this.ctx = ctx
        this.wifiManager = wifiManager
        this.wifiDirectInfo = wifiDirectInfo
        createComponents() //not the best solution to do it in constructor
    }

    private fun createComponents() {
        createMessagingServer()
    }

    private fun createMessagingServer() {
        messagingServer = MessagingServer(ctx)
    }

    override fun startMessagingServer(isGroupOwner: Boolean) {
        if (!messagingServerStarted) {
            Thread(MulticastMessageListener(wifiManager, isGroupOwner))
        }
        messagingServerStarted = true
    }

    override fun sendMessageTo(client: String, message: Message) {
        Thread(SendMessageRunnable(wifiDirectInfo, client, message)).start()
    }

    override fun sendToAll(message: Message) {
        Thread(SendMulticastRunnable(message)).start()
    }
}