package org.thaliproject.p2p.wifidirectdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.support.v4.app.FragmentActivity
import android.util.Log
import timber.log.Timber

class WifiDirectReceiver(val wifiP2pManager: WifiP2pManager,
                         val channel: WifiP2pManager.Channel,
                         val activity: FragmentActivity,
                         val peerListListener: WifiP2pManager.PeerListListener,
                         val connectionInfoListener: WifiP2pManager.ConnectionInfoListener?) : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        Timber.d("action = " + action)
        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                //TODO notify UI about state
                Timber.d("WIFI_P2P_STATE_CHANGED_ACTION")
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                Timber.d("new peers")

//                wifiP2pManager.requestPeers(channel, peerListListener)
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                Timber.d("WIFI_P2P_CONNECTION_CHANGED_ACTION")
                val networkInfo = intent?.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                Timber.d("network info $networkInfo")

                if (networkInfo != null && networkInfo.isConnected) {
//                    if (con)
                    wifiP2pManager.requestConnectionInfo(channel, object : WifiP2pManager.ConnectionInfoListener {
                        override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
                            if (info != null) {
                                if (info.isGroupOwner) {
                                    Timber.d("Start server")
                                    startServer()
                                }
                            }
                        }
                    })
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                Timber.d("WIFI_P2P_THIS_DEVICE_CHANGED_ACTION")
            }
            WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION -> {
                Timber.d("WIFI_P2P_DISCOVERY_CHANGED_ACTION")

                Timber.d("discovery changed")
            }


        }
    }

    private fun startServer() {
        ServerAsyncTask().execute()
    }


}