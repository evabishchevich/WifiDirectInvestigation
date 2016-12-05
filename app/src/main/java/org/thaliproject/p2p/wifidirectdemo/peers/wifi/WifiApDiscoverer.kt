package org.thaliproject.p2p.wifidirectdemo.peers.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import org.thaliproject.p2p.wifidirectdemo.peers.PeersDiscoverer
import timber.log.Timber

class WifiApDiscoverer(val ctx: Context, val wifiManager: WifiManager) : PeersDiscoverer {

    private val wifiNetworksScanner = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("WifiApDiscoverer onreceive, ${intent?.action}")
            if (intent?.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                val results = wifiManager.scanResults
                peersListener.onPeersDiscovered(results.map(::WifiAP))
                if (results.size == 0){
                    wifiManager.startScan()
                }
            }
        }
    }

    private lateinit var peersListener: PeersDiscoverer.PeersDiscoverListener
    private var registered = false

    override fun startDiscovery(listener: PeersDiscoverer.PeersDiscoverListener) {
        ctx.registerReceiver(wifiNetworksScanner, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        registered = true
        peersListener = listener
        wifiManager.startScan()
    }

    override fun onStop() {
        if (registered) {
            ctx.unregisterReceiver(wifiNetworksScanner)
        }
        registered = false
    }
}