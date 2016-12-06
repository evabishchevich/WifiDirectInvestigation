package org.thaliproject.p2p.wifidirectdemo.service.location

interface LocationPermissionService {

    fun isLocationPermissionGranted(): Boolean

    fun requestLocationPermission(listener: OnPermissionRequestListener)

    interface OnPermissionRequestListener {

        fun onGranted()

        fun onDenied()
    }
}