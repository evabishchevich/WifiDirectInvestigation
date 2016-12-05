package org.thaliproject.p2p.wifidirectdemo.peers

interface PeersDiscoverer {

    fun startDiscovery(listener: PeersDiscoverListener)

    fun onStop()

    interface PeersDiscoverListener {

        fun onPeersDiscovered(peers: List<Peer>)
    }
}