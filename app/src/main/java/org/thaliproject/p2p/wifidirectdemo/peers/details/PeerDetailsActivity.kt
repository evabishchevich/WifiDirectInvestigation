package org.thaliproject.p2p.wifidirectdemo.peers.details

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import org.thaliproject.p2p.wifidirectdemo.R

class PeerDetailsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peer_details)
        supportFragmentManager.beginTransaction().replace(R.id.peer_details_fl_placeholder,
                PeerDetailsFragment.newInstance(intent.extras.getString("x"), intent.extras.getString("y"))).addToBackStack(null).commit()
    }
}