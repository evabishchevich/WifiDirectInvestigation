package org.thaliproject.p2p.wifidirectdemo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.SupplicantState
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import org.thaliproject.p2p.wifidirectdemo.peers.wifi.WifiAP
import timber.log.Timber

internal class WifiConnector(val ctx: Context, val wifiManager: WifiManager) {

    private val wifiStateChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("Intent ${intent?.action}")
            if (intent?.action == WifiManager.SUPPLICANT_STATE_CHANGED_ACTION ||
                    intent?.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                val state = intent?.getParcelableExtra<SupplicantState>(WifiManager.EXTRA_NEW_STATE)
                Timber.d("onReceive: state  $state")
                if (state == SupplicantState.COMPLETED) {
                    getSSID()
                }
            } else {
                val netInfo = intent?.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                Timber.d("onReceive: netInfo  $netInfo")
                if (netInfo != null && netInfo.isConnected) {
                    getSSID()
                }
            }
        }
    }

    private var apToConnect: WifiAP? = null
    private var connectionListener: WifiService.ConnectionListener? = null

    init {
        registerWifiStateChangedReceiver()
    }

    private fun registerWifiStateChangedReceiver() {
        val intentFilter = IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        ctx.registerReceiver(wifiStateChangedReceiver, intentFilter)
    }

    fun connectTo(accessPoint: WifiAP, connectionListener: WifiService.ConnectionListener): Boolean {
        Timber.d("Connect to $accessPoint")
        assignConnectionData(accessPoint, connectionListener)
        registerWifiStateChangedReceiver()

        //remember id
        val netId = wifiManager.addNetwork(createWifiConfig());
        Timber.d("Disconnected: ${wifiManager.disconnect()}")
        Timber.d("Enabled: ${wifiManager.enableNetwork(netId, true)}")
//        Timber.d("Config is saved: ${wifiManager.saveConfiguration()}")
        val reconnected = wifiManager.reconnect()
        Timber.d("Reconnected: $reconnected")
        return reconnected
    }

    private fun assignConnectionData(accessPoint: WifiAP, connectionListener: WifiService.ConnectionListener) {
        apToConnect = accessPoint
        this.connectionListener = connectionListener
    }

    private fun releaseConnectionData() {
        apToConnect = null
        connectionListener = null
    }

    private fun createWifiConfig(): WifiConfiguration {
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = "\"${apToConnect?.SSID}\""
        wifiConfig.preSharedKey = "\"${apToConnect?.password}\""
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        return wifiConfig
    }

    private fun getSSID() {
        val connectionInfo = wifiManager.connectionInfo
        val ssid = connectionInfo?.ssid
        Timber.d("onReceive: ssid  $ssid")
        informAboutSSID(ssid)
    }

    private fun informAboutSSID(ssid: String?) {
        if (ssid == "\"${apToConnect?.SSID}\"") {
            onConnectedToAP()
        }
    }

    private fun onConnectedToAP() {
        Timber.d("Connected to AP! Can send multicast")
        connectionListener?.onConnected(apToConnect!!)
        releaseConnectionData()
    }

}