package org.thaliproject.p2p.wifidirectdemo.service

import android.net.wifi.p2p.WifiP2pManager
import org.thaliproject.p2p.wifidirectdemo.DefaultActionListener
import org.thaliproject.p2p.wifidirectdemo.peers.Peer
import org.thaliproject.p2p.wifidirectdemo.peers.wifi.WifiAP

interface WifiService {


    fun createGroup(actionListener: DefaultActionListener)

    fun removeGroup(actionListener: DefaultActionListener)

    fun requestGroupInfo(groupInfoListener: WifiP2pManager.GroupInfoListener)

    fun findNetworks(networksListener: NetworksAvailableListener)

    fun connect(accessPoint: WifiAP, connectionListener: ConnectionListener)

    interface NetworksAvailableListener {
        fun onNetworksAvailable(networks: List<WifiAP>)
    }

    interface ConnectionListener {
        fun onConnected(wifiAP: WifiAP)
    }
}