package org.thaliproject.p2p.wifidirectdemo.peers

import org.thaliproject.p2p.wifidirectdemo.GroupCredits

interface PeersContract {
    interface View {

        fun showGroupCredits(groupCredits: GroupCredits)

        fun setDeviceName(deviceName: String) //not the best way, have to do it via interactor

        fun showNewPeers(peers: List<Peer>)
    }

    interface Presenter {

        fun onStartDiscoveryClicked()

        fun onCreateGroupClicked()

        fun onStop()

    }

}