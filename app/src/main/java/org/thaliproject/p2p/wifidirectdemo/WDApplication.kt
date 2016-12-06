package org.thaliproject.p2p.wifidirectdemo

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import org.thaliproject.p2p.wifidirectdemo.peers.details.RegularAPDetailsFragment
import org.thaliproject.p2p.wifidirectdemo.peers.wifi.WifiApDiscoverer
import org.thaliproject.p2p.wifidirectdemo.service.WifiService
import org.thaliproject.p2p.wifidirectdemo.service.WifiServiceImpl
import org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery.GroupIpsInfo
import org.thaliproject.p2p.wifidirectdemo.service.messaging.MessagingService
import org.thaliproject.p2p.wifidirectdemo.service.messaging.MessagingServiceImpl
import timber.log.Timber

class WDApplication : Application() {

    lateinit var wifiDirectState: WifiDirectState
        private set

    lateinit var groupIpsInfo: GroupIpsInfo
    lateinit var messagingService: MessagingService
    lateinit var wifiService: WifiService

    override fun onCreate() {
        super.onCreate()
        initTimber()
        initWifiDirect()
        initMessagingProvider()
        initWifiService()
    }

    private fun initTimber() {
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement?): String {
                return super.createStackElementTag(element) + ":" + element?.lineNumber
            }
        })
    }

    private fun initWifiDirect() {
        val wifiInfo = initWifiManager()
        val receiver = WifiDirectReceiver(wifiInfo)
        registerReceiver(receiver, createIntentFilter())
        wifiDirectState = WifiDirectState(wifiInfo, receiver)
    }

    private fun initMessagingProvider() {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        messagingService = MessagingServiceImpl(this, wifiManager, wifiDirectState.wifiDirectInfo)
    }

    private fun initWifiService() {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiService = WifiServiceImpl(this, wifiDirectState.wifiDirectInfo, wifiManager, WifiApDiscoverer(this, wifiManager))
    }

    private fun initWifiManager(): WifiDirectInfo {
        val wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        val channel = wifiP2pManager.initialize(this, Looper.getMainLooper(), object : WifiP2pManager.ChannelListener {
            override fun onChannelDisconnected() {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        return WifiDirectInfo(wifiP2pManager, channel)
    }

    private fun createIntentFilter(): IntentFilter {
        val filter = IntentFilter()
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        filter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)
        return filter
    }

    override fun onTerminate() {
        unregisterReceiver(wifiDirectState.stateReceiver)
    }
}