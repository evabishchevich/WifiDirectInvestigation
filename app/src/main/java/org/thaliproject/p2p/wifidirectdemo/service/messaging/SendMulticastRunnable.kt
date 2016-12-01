package org.thaliproject.p2p.wifidirectdemo.service.messaging

import android.net.wifi.WifiManager
import org.thaliproject.p2p.wifidirectdemo.service.MulticastSettings
import timber.log.Timber
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MulticastSocket

class SendMulticastRunnable(val message: Message) : Runnable {

    override fun run() {
        val socket = MulticastSocket(MulticastSettings.MULTICAST_PORT)
        val address = InetAddress.getByName(MulticastSettings.MULTICAST_GROUP_ADDRESS)
        socket.joinGroup(address)
        try {
            val buffer = message.data.toByteArray()
            val packet = DatagramPacket(buffer, buffer.size, address, MulticastSettings.MULTICAST_PORT)
            packet.data = buffer
            socket.send(packet)
            Timber.d("Send ${String(packet.data)}")
        } finally {
            socket.close()
        }
    }
}