package org.thaliproject.p2p.wifidirectdemo

import android.net.wifi.p2p.WifiP2pInfo

interface ConnectionListener {

    fun onConnected(info: WifiP2pInfo)

    fun onDisconnected()

}
