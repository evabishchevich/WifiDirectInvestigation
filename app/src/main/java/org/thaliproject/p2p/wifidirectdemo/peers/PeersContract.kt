package org.thaliproject.p2p.wifidirectdemo.peers

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

    interface PermissionService {

        fun isLocationPermissionGranted(): Boolean

        fun requestLocationPermission(listener: OnPermissionRequestListener)

        interface OnPermissionRequestListener {

            fun onGranted()

            fun onDenied()
        }
    }
}