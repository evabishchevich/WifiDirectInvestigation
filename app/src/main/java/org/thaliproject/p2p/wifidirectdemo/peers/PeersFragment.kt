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
import org.thaliproject.p2p.wifidirectdemo.GroupCredits
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


class PeersFragment : BaseFragment(), PeersContract.View {

    internal lateinit var peersAdapter: PeersAdapter
    private lateinit var rvPeers: RecyclerView

    private lateinit var wifiManager: WifiManager
    private lateinit var peersPresenter: PeersPresenter


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
        val peersDiscoverer = WifiApDiscoverer(activity.applicationContext, wifiManager)
        peersPresenter = PeersPresenter(this, peersDiscoverer, this, wifiDirectState, wifiManager)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_peer, container, false);
        rvPeers = view?.findViewById(R.id.peers_rv_peers) as RecyclerView
        val adapter = RegularWifiPeersAdapter(mutableListOf(), regularPeerClickListener)
        peersAdapter = adapter
        rvPeers.adapter = adapter
        rvPeers.layoutManager = LinearLayoutManager(activity)
        view?.findViewById(R.id.peers_btn_start_discovery)?.setOnClickListener { peersPresenter.onStartDiscoveryClicked() }
        view?.findViewById(R.id.peers_btn_create_group)?.setOnClickListener { peersPresenter.onCreateGroupClicked() }

        return view;
    }

    override fun showGroupCredits(groupCredits: GroupCredits) {
        (view?.findViewById(R.id.peers_tv_group_ssid) as TextView).text = groupCredits.networkName
        (view?.findViewById(R.id.peers_tv_group_pass) as TextView).text = groupCredits.passphrase
    }

    override fun setDeviceName(deviceName: String) {
        val m = wifiDirectState.wifiDirectInfo.wifiP2pManager.javaClass.getMethod(
                "setDeviceName",
                *arrayOf(WifiP2pManager.Channel::class.java, String::class.java, WifiP2pManager.ActionListener::class.java))

        m.invoke(wifiDirectState.wifiDirectInfo.wifiP2pManager, wifiDirectState.wifiDirectInfo.channel,
                deviceName, DefaultActionListener("Device name changed", "Device name NOT changed"))
    }

    override fun showNewPeers(peers: List<Peer>) {
        peersAdapter.data.clear()
        peersAdapter.data.addAll(peers)
        peersAdapter.notifyDataSetChanged()
    }

    override fun onStop() {
        peersPresenter.onStop()
        super.onStop()
    }
}