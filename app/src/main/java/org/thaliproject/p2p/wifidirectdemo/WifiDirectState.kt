package org.thaliproject.p2p.wifidirectdemo

import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager

class WifiDirectState(val wifiDirectInfo: WifiDirectInfo, val stateReceiver: WifiDirectReceiver) :
        ConnectionListener {

    init {
        stateReceiver.connectionListener = this
    }

    private val peerAvailableListeners = mutableListOf<WifiP2pManager.PeerListListener>()
    private val connectionInfoListeners = mutableListOf<WifiP2pManager.ConnectionInfoListener>()

    override fun onConnected(info: WifiP2pInfo) {
        notifyConnectionInfoListeners(info)
    }

    override fun onDisconnected() {
        //Do nothing
    }

    private fun notifyConnectionInfoListeners(info: WifiP2pInfo) {
        for (listener in connectionInfoListeners) {
            listener.onConnectionInfoAvailable(info)
        }
    }

    fun addPeerListener(listener: WifiP2pManager.PeerListListener) {
        peerAvailableListeners.add(listener)
    }

    fun removePeerListener(listener: WifiP2pManager.PeerListListener) {
        peerAvailableListeners.remove(listener)
    }

    fun addConnectionInfoListener(listener: WifiP2pManager.ConnectionInfoListener) {
        connectionInfoListeners.add(listener)
    }

    fun removeConnectionInfoListener(listener: WifiP2pManager.ConnectionInfoListener) {
        connectionInfoListeners.remove(listener)
    }

    //TODO move it to another object
    fun connectTo(address: String, listener: WifiP2pManager.ActionListener) {
        wifiDirectInfo.wifiP2pManager.connect(wifiDirectInfo.channel, makeConfig(address), listener)
    }

    fun requestConnectionInfo(listener: WifiP2pManager.ConnectionInfoListener) {
        wifiDirectInfo.wifiP2pManager.requestConnectionInfo(wifiDirectInfo.channel, listener)
    }

    private fun makeConfig(address: String): WifiP2pConfig {
        val config = WifiP2pConfig()
        config.deviceAddress = address
        config.wps.setup = WpsInfo.PBC
        config.groupOwnerIntent = 0
        return config
    }

    fun getGroupInfo(groupInfoListener: WifiP2pManager.GroupInfoListener) {
        wifiDirectInfo.wifiP2pManager.requestGroupInfo(wifiDirectInfo.channel, groupInfoListener)
    }


}