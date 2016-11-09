package org.thaliproject.p2p.wifidirectdemo.peers.service

import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo

class DemoService() {

    fun provideDnsServiceInfo(): WifiP2pDnsSdServiceInfo {
        val record = mapOf(Pair(TXTRECORD_PROP_AVAILABLE, "visible"))
        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_NAME, SERVICE_REG_TYPE, record)
        return serviceInfo
    }

    companion object {
        val SERVICE_NAME = "demoService"
        val SERVICE_REG_TYPE = "_thali._tcp"
        val TXTRECORD_PROP_AVAILABLE = "available"
    }
}