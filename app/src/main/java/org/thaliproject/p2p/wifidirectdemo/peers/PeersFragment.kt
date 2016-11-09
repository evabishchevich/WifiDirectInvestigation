package org.thaliproject.p2p.wifidirectdemo.peers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import org.thaliproject.p2p.wifidirectdemo.R
import org.thaliproject.p2p.wifidirectdemo.WDApplication
import org.thaliproject.p2p.wifidirectdemo.WDLog
import org.thaliproject.p2p.wifidirectdemo.WifiDirectReceiver
import org.thaliproject.p2p.wifidirectdemo.peers.details.PeerDetailsActivity
import org.thaliproject.p2p.wifidirectdemo.peers.service.DemoService
import timber.log.Timber


class PeersFragment : Fragment() {

    lateinit var wifiDirectReceiver: WifiDirectReceiver
    lateinit var wifiP2pManager: WifiP2pManager
    lateinit var channel: WifiP2pManager.Channel

    lateinit var adapter: PeersAdapter
    lateinit var wifiFilter: IntentFilter
    lateinit var rvPeers: RecyclerView

    val peerClickListener = object : PeersAdapter.OnPeerClickListener {
        override fun onPeerClicked(peer: WifiP2pDevice) {
            Timber.d("onPeerClicked " + peer.toString())
            (activity.application as WDApplication).wifiP2pManager = wifiP2pManager
            (activity.application as WDApplication).channel = channel

            val intent = Intent(activity, PeerDetailsActivity::class.java)
            intent.putExtra("x", peer.deviceName)
            intent.putExtra("y", peer.deviceAddress)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        wifiP2pManager = activity.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager.initialize(activity, Looper.getMainLooper(), object : WifiP2pManager.ChannelListener {
            override fun onChannelDisconnected() {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })


        wifiDirectReceiver = WifiDirectReceiver(wifiP2pManager, channel, activity, object : WifiP2pManager.PeerListListener {
            override fun onPeersAvailable(peers: WifiP2pDeviceList?) {
                Timber.d("peers: " + peers?.toString())
//                val start = adapter.data.size
//                adapter.data.clear()
//                adapter.data.addAll(peers?.deviceList!!)
//                adapter.notifyItemRangeInserted(start, adapter.data.size)

            }
        })
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_peer, container, false);
        rvPeers = view?.findViewById(R.id.peers_rv_peers) as RecyclerView
        adapter = PeersAdapter(mutableListOf(), peerClickListener)
        rvPeers.adapter = adapter
        rvPeers.layoutManager = LinearLayoutManager(activity)
        view?.findViewById(R.id.peers_btn_start_discovery)?.setOnClickListener { startDiscovery() }
        view?.findViewById(R.id.peers_btn_create_group)?.setOnClickListener { createGroup() }
        return view;
    }

    private fun startDiscovery() {
        WifiPeersDiscoverer(activity, wifiP2pManager, channel, object : WifiPeersDiscoverer.OnPeerListener {
            override fun onDiscovered(peer: WifiP2pDevice) {
                Timber.d(" New peer ${peer.toString()}")
//                adapter.data.clear()
                adapter.data.add(peer)
                adapter.notifyDataSetChanged()
            }
        }).discoverSpecialService()
//        Handler().postDelayed({ WifiPeersDiscoverer(activity, wifiP2pManager, channel).discoverSpecialService() }, 3000L)
    }

    private fun createGroup() {
        //TODO create a group
        wifiP2pManager.createGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Timber.d("group successfully created")
            }

            override fun onFailure(reason: Int) {
                Timber.d("group creation failed. reason $reason ")
//                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    override fun onResume() {
        super.onResume()
        activity.registerReceiver(wifiDirectReceiver, createIntentFilter())
    }


    override fun onPause() {
        super.onPause()
        activity.unregisterReceiver(wifiDirectReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_action_find_peers -> {
                //TODO call the findPeers action
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}