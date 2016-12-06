package org.thaliproject.p2p.wifidirectdemo.peers.wifi

import android.net.wifi.ScanResult
import org.thaliproject.p2p.wifidirectdemo.peers.Peer
import org.thaliproject.p2p.wifidirectdemo.service.GroupSettings


class WifiAP(scanResult: ScanResult) : Peer() {

    val SSID: String
    val password: String

    init {
        SSID = scanResult.SSID
        password = GroupSettings.GROUP_PASSWORD
    }
}