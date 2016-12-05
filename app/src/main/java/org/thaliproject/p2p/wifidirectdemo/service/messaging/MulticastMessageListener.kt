package org.thaliproject.p2p.wifidirectdemo.service.messaging

import android.net.wifi.WifiManager
import org.thaliproject.p2p.wifidirectdemo.service.MulticastSettings
import timber.log.Timber
import java.net.*

class MulticastMessageListener(val wifiManager: WifiManager, val isGroupOwner: Boolean) : Runnable {

    private val MULTICAST_LOCK_TAG = "org.thaliproject.p2p.wifidirectdemo.service.MULTICAST_LOCK_TAG"

    override fun run() {
        val multiCastLock = wifiManager.createMulticastLock(MULTICAST_LOCK_TAG)
        try {
            multiCastLock.acquire()
            val buffer = ByteArray(MulticastSettings.MULTICAST_BUFFER_SIZE)
            val packet = DatagramPacket(buffer, buffer.size)
            val receiveSocket = MulticastSocket(MulticastSettings.MULTICAST_PORT)
            val group = InetAddress.getByName(MulticastSettings.MULTICAST_GROUP_ADDRESS)
//            receiveSocket.`interface` = group
            for (netInterface in NetworkInterface.getNetworkInterfaces()) {
                Timber.d("Network interface : $netInterface")
            }
            val socketAddr = InetSocketAddress(group, MulticastSettings.MULTICAST_PORT)
            val netInt = if (isGroupOwner) NetworkInterface.getByName("p2p-wlan0-0") else NetworkInterface.getByName("wlan0")
            Timber.d("Network interface: $netInt")
            receiveSocket.joinGroup(socketAddr, netInt)
//            receiveSocket.joinGroup(group)
//            receiveSocket.broadcast = true
            Timber.d("Receiving started")
            while (true) { //TODO make it flexible
                receiveSocket.receive(packet)
                Timber.d("Received : from ${packet.address} ${String(packet.data)}")
            }
        } finally {
            multiCastLock.release()
        }
    }
}