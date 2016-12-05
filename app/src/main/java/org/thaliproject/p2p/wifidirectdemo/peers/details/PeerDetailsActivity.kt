package org.thaliproject.p2p.wifidirectdemo.peers.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import org.thaliproject.p2p.wifidirectdemo.R
import timber.log.Timber

class PeerDetailsActivity : FragmentActivity() {

    companion object {
        private val DEVICE_NAME_KEY = "DEVICE_NAME_KEY"
        private val DEVICE_ADDRESS_KEY = "DEVICE_ADDRESS_KEY"
        private val SSID_KEY = "SSID_KEY"

        fun startActivity(who: Context, deviceName: String, deviceAddress: String) {
            val intent = Intent(who, PeerDetailsActivity::class.java)
            putArgs(intent, deviceName, deviceAddress)
            who.startActivity(intent)
        }

        fun startActivity(who: Context, ssid: String) {
            val intent = Intent(who, PeerDetailsActivity::class.java)
            putArgs(intent, ssid)
            who.startActivity(intent)
        }

        private fun putArgs(intent: Intent, deviceName: String, deviceAddress: String) {
            intent.putExtra(DEVICE_NAME_KEY, deviceName)
            intent.putExtra(DEVICE_ADDRESS_KEY, deviceAddress)
        }

        private fun putArgs(intent: Intent, ssid: String) {
            intent.putExtra(SSID_KEY, ssid)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peer_details)
        if (intent.extras.getString(DEVICE_NAME_KEY) != null && intent.extras.getString(DEVICE_ADDRESS_KEY) != null) {
            Timber.d("start P2pPeerDetailsFragment")
            supportFragmentManager.beginTransaction().replace(R.id.peer_details_fl_placeholder,
                    P2pPeerDetailsFragment.newInstance(intent.extras.getString(DEVICE_NAME_KEY), intent.extras.getString(DEVICE_ADDRESS_KEY))).commit()
        } else {
            Timber.d("start RegularAPDetailsFragment")
            supportFragmentManager.beginTransaction().replace(R.id.peer_details_fl_placeholder,
                    RegularAPDetailsFragment.newInstance(intent.extras.getString(SSID_KEY))).commit()
        }
    }

}