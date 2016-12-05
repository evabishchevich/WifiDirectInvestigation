package org.thaliproject.p2p.wifidirectdemo.peers.wifi

import android.net.wifi.ScanResult
import org.thaliproject.p2p.wifidirectdemo.peers.Peer


class WifiAP(scanResult: ScanResult) : Peer() {

    val SSID: String

    init {
        SSID = scanResult.SSID
    }
}