package org.thaliproject.p2p.wifidirectdemo.service.messaging

import org.thaliproject.p2p.wifidirectdemo.IpInfo
import org.thaliproject.p2p.wifidirectdemo.WifiDirectInfo
import org.thaliproject.p2p.wifidirectdemo.service.GroupSettings
import timber.log.Timber
import java.net.InetSocketAddress
import java.net.Socket

class SendMessageRunnable(val wifiDirectInfo: WifiDirectInfo, val clientAddress: String, val message: Message) : Runnable {

    override fun run() {
        val port = GroupSettings.MESSAGING_SERVER_PORT
        val socket = Socket()
        try {
            socket.connect(InetSocketAddress(clientAddress, port), GroupSettings.CONNECTION_TIMEOUT)
            wifiDirectInfo.ipInfo = IpInfo(socket.localAddress.hostAddress)
            Timber.d("current ip : ${socket.localAddress.hostAddress}")
            socket.outputStream.write(message.data.toByteArray())
            Timber.d("${message.data} was written")
        } finally {
            socket.close()
        }
    }
}