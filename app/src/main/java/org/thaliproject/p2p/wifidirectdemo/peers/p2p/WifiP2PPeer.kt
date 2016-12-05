package org.thaliproject.p2p.wifidirectdemo.peers.p2p

import android.net.wifi.p2p.WifiP2pDevice
import org.thaliproject.p2p.wifidirectdemo.peers.Peer

class WifiP2PPeer(wifiP2PPeer: WifiP2pDevice) : Peer() {
    val deviceName: String
    val deviceAddress: String

    init {
        deviceName = wifiP2PPeer.deviceName
        deviceAddress = wifiP2PPeer.deviceAddress
    }
}