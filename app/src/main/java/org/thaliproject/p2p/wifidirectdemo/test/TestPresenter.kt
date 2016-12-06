package org.thaliproject.p2p.wifidirectdemo.test

import android.net.wifi.p2p.WifiP2pManager
import android.os.SystemClock
import org.thaliproject.p2p.wifidirectdemo.DefaultActionListener
import org.thaliproject.p2p.wifidirectdemo.GroupCredits
import org.thaliproject.p2p.wifidirectdemo.Settings
import org.thaliproject.p2p.wifidirectdemo.peers.wifi.WifiAP
import org.thaliproject.p2p.wifidirectdemo.service.WifiService
import org.thaliproject.p2p.wifidirectdemo.service.WifiService.ConnectionListener
import org.thaliproject.p2p.wifidirectdemo.service.location.LocationPermissionService
import org.thaliproject.p2p.wifidirectdemo.service.messaging.MessagingService
import timber.log.Timber

class TestPresenter(val view: TestContract.View,
                    val messagingService: MessagingService,
                    val wifiService: WifiService,
                    val locationPermissionService: LocationPermissionService) : TestContract.Presenter {

    private var startTime: Long = 0
    private var finishTime: Long = 0

    override fun onStartServerClicked() {
        createGroup()
    }

    private fun createGroup() {
        view.setDeviceName(Settings.DEVICE_NAME)
        wifiService.createGroup(object : DefaultActionListener("group successfully created", "group creation failed") {
            override fun onSuccess() {
                super.onSuccess()
                requestGroupInfo()
                startMulticastsListening(true)
            }

            override fun onFailure(reason: Int) {
                super.onFailure(reason)
                removeGroup()
            }
        })
    }

    private fun startMulticastsListening(isGroupOwner: Boolean) {
        messagingService.startMessagingServer(isGroupOwner)
    }

    private fun removeGroup() {
        wifiService.removeGroup(object : DefaultActionListener("group successfully removed", "group deletion failed") {
            override fun onSuccess() {
                super.onSuccess()
                createGroup()
            }

            override fun onFailure(reason: Int) {
                super.onFailure(reason)
            }
        })
    }

    private fun requestGroupInfo() {
        wifiService.requestGroupInfo(WifiP2pManager.GroupInfoListener { group ->
            Timber.d("Group info available $group")
            if (group == null) {
                requestGroupInfo()
            } else {
                view.showGroupCredits(GroupCredits(group.networkName, group.passphrase))
            }
        })
    }

    override fun onStartClientClicked() {
        if (locationPermissionService.isLocationPermissionGranted()) {
            startTime = SystemClock.elapsedRealtime();
            startDiscovery()
        } else {
            locationPermissionService.requestLocationPermission(object : LocationPermissionService.OnPermissionRequestListener {
                override fun onGranted() {
                    Timber.e("location permission is granted")
                    startDiscovery()
                }

                override fun onDenied() {
                    Timber.e("LOCATION PERMISSION DENIED")
                }
            })
        }
    }

    private fun startDiscovery() {
        wifiService.findNetworks(object : WifiService.NetworksAvailableListener {
            override fun onNetworksAvailable(networks: List<WifiAP>) {
                val wifiNetwork = networks.filter { it -> it.SSID.contains(Settings.DEVICE_NAME) }.first()
                wifiService.connect(wifiNetwork, object : ConnectionListener {
                    override fun onConnected(wifiAP: WifiAP) {
                        messagingService.startMessagingServer(false)
                        finishTime = SystemClock.elapsedRealtime()
                        view.showTotalDuration("Tital duration is ${finishTime - startTime} milliseconds")
                        //TODO send data
                        //TODO get data back
                    }
                })
            }
        })
    }

}
