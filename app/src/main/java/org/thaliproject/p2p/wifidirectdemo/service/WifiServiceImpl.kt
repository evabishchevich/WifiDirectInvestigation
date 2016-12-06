package org.thaliproject.p2p.wifidirectdemo.service

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import org.thaliproject.p2p.wifidirectdemo.DefaultActionListener
import org.thaliproject.p2p.wifidirectdemo.WifiDirectInfo
import org.thaliproject.p2p.wifidirectdemo.peers.Peer
import org.thaliproject.p2p.wifidirectdemo.peers.PeersDiscoverer
import org.thaliproject.p2p.wifidirectdemo.peers.wifi.WifiAP
import timber.log.Timber

class WifiServiceImpl(ctx: Context,
                      val wifiDirectInfo: WifiDirectInfo,
                      val wifiManager: WifiManager,
                      val peersDiscoverer: PeersDiscoverer) : WifiService {

    private val wifiConnector: WifiConnector

    init {
        wifiConnector = WifiConnector(ctx, wifiManager)
    }

    override fun createGroup(actionListener: DefaultActionListener) {
        wifiDirectInfo.wifiP2pManager.createGroup(wifiDirectInfo.channel,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        actionListener.onSuccess()
                    }

                    override fun onFailure(reason: Int) {
                        actionListener.onFailure(reason)
                    }
                })
    }

    override fun removeGroup(actionListener: DefaultActionListener) {
        wifiDirectInfo.wifiP2pManager.removeGroup(wifiDirectInfo.channel,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        actionListener.onSuccess()
                    }

                    override fun onFailure(reason: Int) {
                        actionListener.onFailure(reason)
                    }
                })
    }

    override fun requestGroupInfo(groupInfoListener: WifiP2pManager.GroupInfoListener) {
        wifiDirectInfo.wifiP2pManager.requestGroupInfo(wifiDirectInfo.channel) {
            group ->
            groupInfoListener.onGroupInfoAvailable(group)
        }
    }

    override fun findNetworks(networksListener: WifiService.NetworksAvailableListener) {
        Timber.d("findNetworks")
        peersDiscoverer.startDiscovery(object : PeersDiscoverer.PeersDiscoverListener {
            override fun onPeersDiscovered(peers: List<Peer>) {
                networksListener.onNetworksAvailable(peers.map { it -> it as WifiAP })
            }
        }, true)
    }

    override fun connect(accessPoint: WifiAP, connectionListener: WifiService.ConnectionListener) {
        wifiConnector.connectTo(accessPoint, connectionListener)
    }

}