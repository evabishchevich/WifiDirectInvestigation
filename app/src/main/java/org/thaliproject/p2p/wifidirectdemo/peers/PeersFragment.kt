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
import android.widget.Toast
import org.thaliproject.p2p.wifidirectdemo.*
import org.thaliproject.p2p.wifidirectdemo.peers.details.PeerDetailsActivity
import org.thaliproject.p2p.wifidirectdemo.peers.service.DemoService
import timber.log.Timber


class PeersFragment : BaseFragment() {

    lateinit var adapter: PeersAdapter
    lateinit var rvPeers: RecyclerView

    private var serverStarted = false

    companion object {
        val DEVICE_NAME = "ThaliGroup"
    }

    val peerClickListener = object : PeersAdapter.OnPeerClickListener {
        override fun onPeerClicked(peer: WifiP2pDevice) {
            Timber.d("onPeerClicked " + peer.toString())
            val intent = Intent(activity, PeerDetailsActivity::class.java)
            intent.putExtra("x", peer.deviceName)
            intent.putExtra("y", peer.deviceAddress)
            startActivity(intent)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_peer, container, false);
        rvPeers = view?.findViewById(R.id.peers_rv_peers) as RecyclerView
        adapter = PeersAdapter(mutableListOf(), peerClickListener)
        rvPeers.adapter = adapter
        rvPeers.layoutManager = LinearLayoutManager(activity)
        view?.findViewById(R.id.peers_btn_start_discovery)?.setOnClickListener { startDiscovery() }
        view?.findViewById(R.id.peers_btn_create_group)?.setOnClickListener { createGroup() }
        view?.findViewById(R.id.peers_btn_create_access_point)?.setOnClickListener { createAccessPoint() }
        return view;
    }

    private fun createAccessPoint() {
    }

    private fun startDiscovery() {
        WifiPeersDiscoverer(activity, wifiDirectState.wifiDirectInfo.wifiP2pManager, wifiDirectState.wifiDirectInfo.channel, object : WifiPeersDiscoverer.OnPeerListener {
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
        setDeviceName(DEVICE_NAME)
        wifiDirectState.wifiDirectInfo.wifiP2pManager.createGroup(wifiDirectState.wifiDirectInfo.channel,
                object : DefaultActionListener("group successfully created", "group creation failed") {
                    override fun onSuccess() {
                        super.onSuccess()
                        startServer()
                    }

                    override fun onFailure(reason: Int) {
                        super.onFailure(reason)
                        removeGroup()
                    }
                })
    }

    //TODO move to into another object
    private fun removeGroup() {
        wifiDirectState.wifiDirectInfo.wifiP2pManager.removeGroup(wifiDirectState.wifiDirectInfo.channel,
                object : DefaultActionListener("group successfully removed", "group deletion failed") {
                    override fun onSuccess() {
                        super.onSuccess()
                        createGroup()
                    }

                    override fun onFailure(reason: Int) {
                        super.onFailure(reason)
                    }
                })
    }

    private fun startServer() {
        if (serverStarted) {
            throw RuntimeException("Server already started")
        }
        serverStarted = true
        ServerAsyncTask().execute()
    }

    fun setDeviceName(deviceName: String) {
        val m = wifiDirectState.wifiDirectInfo.wifiP2pManager.javaClass.getMethod(
                "setDeviceName",
                *arrayOf(WifiP2pManager.Channel::class.java, String::class.java, WifiP2pManager.ActionListener::class.java))

        m.invoke(wifiDirectState.wifiDirectInfo.wifiP2pManager, wifiDirectState.wifiDirectInfo.channel,
                deviceName, DefaultActionListener("Device name changed", "Device name NOT changed"))
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