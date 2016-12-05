package org.thaliproject.p2p.wifidirectdemo.peers

internal interface PeersAdapter {

    val data: MutableList<Peer>

    fun notifyDataSetChanged()
}