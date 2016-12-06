package org.thaliproject.p2p.wifidirectdemo

import android.net.wifi.p2p.WifiP2pManager
import timber.log.Timber

open class DefaultActionListener(val successMsg: String, val errorMsg: String = " Error!") : WifiP2pManager.ActionListener {

    override fun onSuccess() {
        Timber.d(successMsg)
    }

    override fun onFailure(reason: Int) {
        Timber.e("$errorMsg. Reason: $reason")
    }
}