package org.thaliproject.p2p.wifidirectdemo.peers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import org.thaliproject.p2p.wifidirectdemo.BaseFragment
import org.thaliproject.p2p.wifidirectdemo.DefaultActionListener
import org.thaliproject.p2p.wifidirectdemo.R
import org.thaliproject.p2p.wifidirectdemo.peers.details.PeerDetailsActivity
import org.thaliproject.p2p.wifidirectdemo.peers.p2p.P2PPeersAdapter
import org.thaliproject.p2p.wifidirectdemo.peers.p2p.WifiP2PPeer
import org.thaliproject.p2p.wifidirectdemo.peers.p2p.WifiP2PPeersDiscoverer
import org.thaliproject.p2p.wifidirectdemo.peers.wifi.RegularWifiPeersAdapter
import org.thaliproject.p2p.wifidirectdemo.peers.wifi.WifiAP
import org.thaliproject.p2p.wifidirectdemo.peers.wifi.WifiApDiscoverer
import org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery.GroupIpsProvider
import org.thaliproject.p2p.wifidirectdemo.service.messaging.MulticastMessageListener
import timber.log.Timber


class PeersFragment : BaseFragment() {

    internal lateinit var peersAdapter: PeersAdapter
    private lateinit var rvPeers: RecyclerView

    private var serverStarted = false
    private lateinit var wifiManager: WifiManager

    private lateinit var peersDiscoverer: PeersDiscoverer

    companion object {
        val DEVICE_NAME = "ThaliGroup"
    }

    val p2pPeerClickListener = object : P2PPeersAdapter.OnPeerClickListener {
        override fun onPeerClicked(peer: WifiP2PPeer) {
            Timber.d("onPeerClicked " + peer.toString())
            PeerDetailsActivity.startActivity(activity, peer.deviceName, peer.deviceAddress)
        }
    }

    val regularPeerClickListener = object : RegularWifiPeersAdapter.OnPeerClickListener {
        override fun onPeerClicked(peer: WifiAP) {
            Timber.d("onPeerClicked " + peer.toString())
            PeerDetailsActivity.startActivity(activity, peer.SSID)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager
        peersDiscoverer = WifiApDiscoverer(activity.applicationContext, wifiManager)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_peer, container, false);
        rvPeers = view?.findViewById(R.id.peers_rv_peers) as RecyclerView
        val adapter = RegularWifiPeersAdapter(mutableListOf(), regularPeerClickListener)
        peersAdapter = adapter
        rvPeers.adapter = adapter
        rvPeers.layoutManager = LinearLayoutManager(activity)
        view?.findViewById(R.id.peers_btn_start_discovery)?.setOnClickListener { startDiscovery() }
        view?.findViewById(R.id.peers_btn_create_group)?.setOnClickListener { createGroup() }

        return view;
    }

    private fun startRegularDiscovery() {
        if (isLocationPermissionGiven()) {
            peersDiscoverer.startDiscovery(object : PeersDiscoverer.PeersDiscoverListener {
                override fun onPeersDiscovered(peers: List<Peer>) {
                    Timber.d("onPeersDiscovered: peers = $peers")
                    peersAdapter.data.clear()
                    peersAdapter.data.addAll(peers)
                    peersAdapter.notifyDataSetChanged()
                }
            })
        } else {
            askForLocationPermission()
        }
    }

    private fun isLocationPermissionGiven(): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private val reqCode = 518

    private fun askForLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), reqCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == reqCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startDiscovery()
        }
    }

    private fun startDiscovery() {
        startRegularDiscovery()
    }

    private fun startP2PDiscovery() {
        WifiP2PPeersDiscoverer(wifiDirectState.wifiDirectInfo.wifiP2pManager, wifiDirectState.wifiDirectInfo.channel, object : WifiP2PPeersDiscoverer.OnPeerListener {
            override fun onDiscovered(peer: WifiP2pDevice) {
                Timber.d(" New peer $peer")
                peersAdapter.data.add(WifiP2PPeer(peer))
                peersAdapter.notifyDataSetChanged()
            }
        }).discoverSpecialService()
//        Handler().postDelayed({ WifiP2PPeersDiscoverer(activity, wifiP2pManager, channel).discoverSpecialService() }, 3000L)
    }

    private fun createGroup() {
        setDeviceName(DEVICE_NAME)
        wifiDirectState.wifiDirectInfo.wifiP2pManager.createGroup(wifiDirectState.wifiDirectInfo.channel,
                object : DefaultActionListener("group successfully created", "group creation failed") {
                    override fun onSuccess() {
                        super.onSuccess()
                        requestGroupInfo()
                        startServer()
                        //TODO temp start multicast listener
                        startMulticastsListening()
                    }

                    override fun onFailure(reason: Int) {
                        super.onFailure(reason)
                        removeGroup()
                    }
                })
    }

    private fun startMulticastsListening() {
        val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager
        Thread(MulticastMessageListener(wifiManager, true)).start()
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
//            throw RuntimeException("Server already started")
            return
        }
        serverStarted = true
        GroupIpsProvider(activity.applicationContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    private fun requestGroupInfo() {
        wifiDirectState.wifiDirectInfo.wifiP2pManager.requestGroupInfo(wifiDirectState.wifiDirectInfo.channel) {
            group ->
            Timber.d("Group info available $group")
            if (group == null) {
                requestGroupInfo()
            } else {
                (view?.findViewById(R.id.peers_tv_group_ssid) as TextView).text = group.networkName
                (view?.findViewById(R.id.peers_tv_group_pass) as TextView).text = group.passphrase
            }
        }
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

    override fun onStop() {
        peersDiscoverer.onStop()
        super.onStop()
    }

}