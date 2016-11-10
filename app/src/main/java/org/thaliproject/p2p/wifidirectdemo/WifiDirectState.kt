package org.thaliproject.p2p.wifidirectdemo

import android.content.Context
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import java.net.NetworkInterface
import java.util.*

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


//    fun getIpAddress(): String {
//        try {
//            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
//            /*
//         * for (NetworkInterface networkInterface : interfaces) { Log.v(TAG,
//         * "interface name " + networkInterface.getName() + "mac = " +
//         * getMACAddress(networkInterface.getName())); }
//         */
//
//            for (intf in interfaces) {
//                if (!getMACAddress(intf.getName()).equalsIgnoreCase(
//                        Globals.thisDeviceAddress)) {
//                    // Log.v(TAG, "ignore the interface " + intf.getName());
//                    // continue;
//                }
//                if (!intf.getName().contains("p2p"))
//                    continue
//
//                Log.v(TAG,
//                        intf.getName() + "   " + getMACAddress(intf.getName()))
//
//                val addrs = Collections.list(intf.getInetAddresses())
//
//                for (addr in addrs) {
//                    // Log.v(TAG, "inside");
//
//                    if (!addr.isLoopbackAddress()) {
//                        // Log.v(TAG, "isnt loopback");
//                        val sAddr = addr.getHostAddress().toUpperCase()
//                        Log.v(TAG, "ip=" + sAddr)
//
//                        val isIPv4 = InetAddressUtils.isIPv4Address(sAddr)
//
//                        if (isIPv4) {
//                            if (sAddr.contains("192.168.49.")) {
//                                Log.v(TAG, "ip = " + sAddr)
//                                return sAddr
//                            }
//                        }
//
//                    }
//
//                }
//            }
//
//        } catch (ex: Exception) {
//            Log.v(TAG, "error in parsing")
//        }
//        // for now eat exceptions
//        Log.v(TAG, "returning empty ip address")
//        return ""
//    }

}