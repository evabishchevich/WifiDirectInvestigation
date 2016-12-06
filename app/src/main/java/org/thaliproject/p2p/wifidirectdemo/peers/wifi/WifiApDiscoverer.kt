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
                if (results.size == 0) {
                    Timber.d("Scan results is empty")
                    wifiManager.startScan()
                    return
                }
                Timber.d(if (peersListener != null) " Listener is not null, notify" else "Listener is null")
                peersListener?.onPeersDiscovered(results.map(::WifiAP))
                if (unsubscribe) {
                    peersListener = null
                }
            }
        }
    }

    private var peersListener: PeersDiscoverer.PeersDiscoverListener? = null
    private var unsubscribe = false
    private var registered = false

    override fun startDiscovery(listener: PeersDiscoverer.PeersDiscoverListener, unsubscribeAfterNotification: Boolean) {
        Timber.d("startDiscovery")
        if (registered) {
            onStop()
        }
        ctx.registerReceiver(wifiNetworksScanner, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        registered = true
        peersListener = listener
        unsubscribe = unsubscribeAfterNotification
        wifiManager.startScan()
    }

    override fun onStop() {
        if (registered) {
            Timber.d("unregisterReceiver")
            ctx.unregisterReceiver(wifiNetworksScanner)
        }
        registered = false
    }
}