package org.thaliproject.p2p.wifidirectdemo

import android.app.Application
import android.net.wifi.p2p.WifiP2pManager
import timber.log.Timber

class WDApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initTimber()
    }

    private fun initTimber() {
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement?): String {
                return super.createStackElementTag(element) + ":" + element?.lineNumber
            }
        })
    }

    //TODO replace it
    lateinit var wifiP2pManager: WifiP2pManager
    lateinit var channel: WifiP2pManager.Channel
}