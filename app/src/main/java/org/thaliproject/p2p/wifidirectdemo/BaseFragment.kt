package org.thaliproject.p2p.wifidirectdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import org.thaliproject.p2p.wifidirectdemo.peers.PeersContract
import org.thaliproject.p2p.wifidirectdemo.service.WifiService
import org.thaliproject.p2p.wifidirectdemo.service.location.LocationPermissionService
import org.thaliproject.p2p.wifidirectdemo.service.messaging.MessagingService

open class BaseFragment : Fragment(), LocationPermissionService {

    private val reqCode = 518
    private lateinit var locationPermissionListener: LocationPermissionService.OnPermissionRequestListener
    protected lateinit var app: WDApplication
        private set
    protected lateinit var wifiDirectState: WifiDirectState
        private set
    protected lateinit var messagingService: MessagingService
        private set
    protected lateinit var wifiService: WifiService
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity.application as WDApplication
        wifiDirectState = (activity.application as WDApplication).wifiDirectState
        messagingService = app.messagingService
        wifiService = app.wifiService
    }

    override fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestLocationPermission(listener: LocationPermissionService.OnPermissionRequestListener) {
        locationPermissionListener = listener
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), reqCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == reqCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationPermissionListener.onGranted()
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            locationPermissionListener.onDenied()
        }
    }
}