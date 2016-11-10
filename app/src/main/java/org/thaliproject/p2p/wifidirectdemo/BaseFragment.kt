package org.thaliproject.p2p.wifidirectdemo

import android.os.Bundle
import android.support.v4.app.Fragment

open class BaseFragment : Fragment() {
    protected lateinit var app: WDApplication
        private set
    protected lateinit var wifiDirectState: WifiDirectState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity.application as WDApplication
        wifiDirectState = (activity.application as WDApplication).wifiDirectState
    }
}