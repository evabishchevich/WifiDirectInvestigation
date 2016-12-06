package org.thaliproject.p2p.wifidirectdemo.peers

import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pDevice
import org.thaliproject.p2p.wifidirectdemo.DefaultActionListener
import org.thaliproject.p2p.wifidirectdemo.GroupCredits
import org.thaliproject.p2p.wifidirectdemo.WifiDirectState
import org.thaliproject.p2p.wifidirectdemo.peers.p2p.WifiP2PPeer
import org.thaliproject.p2p.wifidirectdemo.peers.p2p.WifiP2PPeersDiscoverer
import org.thaliproject.p2p.wifidirectdemo.service.location.LocationPermissionService
import org.thaliproject.p2p.wifidirectdemo.service.messaging.MulticastMessageListener
import timber.log.Timber

class PeersPresenter(val view: PeersContract.View, discoverer: PeersDiscoverer,
                     permissionService: LocationPermissionService, val wifiDirectState: WifiDirectState, //TODO do wifi staff via interactor
                     val wifiManager: WifiManager) : PeersContract.Presenter {

    private companion object {
        val DEVICE_NAME = "ThaliGroup"
    }

    private val peersDiscoverer: PeersDiscoverer
    private val permissionService: LocationPermissionService
//    private var serverStarted = false


    init {
        peersDiscoverer = discoverer
        this.permissionService = permissionService
    }

    override fun onStartDiscoveryClicked() {
        startDiscovery()
    }

    override fun onCreateGroupClicked() {
        createGroup()
    }

    private fun startRegularDiscovery() {
        if (permissionService.isLocationPermissionGranted()) {
            peersDiscoverer.startDiscovery(object : PeersDiscoverer.PeersDiscoverListener {
                override fun onPeersDiscovered(peers: List<Peer>) {
                    Timber.d("onPeersDiscovered: peers = $peers")
                    view.showNewPeers(peers)
                }
            }, false)
        } else {
            permissionService.requestLocationPermission(object : LocationPermissionService.OnPermissionRequestListener {
                override fun onGranted() {
                    Timber.e("location permission is granted")
                    startRegularDiscovery()
                }

                override fun onDenied() {
                    Timber.e("LOCATION PERMISSION DENIED")
                }
            })
        }
    }

    private fun startDiscovery() {
        startRegularDiscovery()
    }

    private fun startP2PDiscovery() {
        WifiP2PPeersDiscoverer(wifiDirectState.wifiDirectInfo.wifiP2pManager, wifiDirectState.wifiDirectInfo.channel, object : WifiP2PPeersDiscoverer.OnPeerListener {
            override fun onDiscovered(peer: WifiP2pDevice) {
                Timber.d(" New peer $peer")
                view.showNewPeers(listOf(WifiP2PPeer(peer)))
            }
        }).discoverSpecialService()
//        Handler().postDelayed({ WifiP2PPeersDiscoverer(activity, wifiP2pManager, channel).discoverSpecialService() }, 3000L)
    }

    private fun createGroup() {
        view.setDeviceName(DEVICE_NAME)
        wifiDirectState.wifiDirectInfo.wifiP2pManager.createGroup(wifiDirectState.wifiDirectInfo.channel,
                object : DefaultActionListener("group successfully created", "group creation failed") {
                    override fun onSuccess() {
                        super.onSuccess()
                        requestGroupInfo()
//                        startServer()
//                        TODO temp start multicast listener
                        startMulticastsListening()
                    }

                    override fun onFailure(reason: Int) {
                        super.onFailure(reason)
                        removeGroup()
                    }
                })
    }

    private fun startMulticastsListening() {
        Thread(MulticastMessageListener(wifiManager, true)).start()
    }

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

//    private fun startServer() {
//        if (serverStarted) {
////            throw RuntimeException("Server already started")
//            return
//        }
//        serverStarted = true
//        GroupIpsProvider(activity.applicationContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
//    }

    private fun requestGroupInfo() {
        wifiDirectState.wifiDirectInfo.wifiP2pManager.requestGroupInfo(wifiDirectState.wifiDirectInfo.channel) {
            group ->
            Timber.d("Group info available $group")
            if (group == null) {
                requestGroupInfo()
            } else {
                view.showGroupCredits(GroupCredits(group.networkName, group.passphrase))
            }
        }
    }

    override fun onStop() {
        peersDiscoverer.onStop()
    }
}