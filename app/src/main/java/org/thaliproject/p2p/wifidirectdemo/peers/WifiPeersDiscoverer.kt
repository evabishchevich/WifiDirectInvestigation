package org.thaliproject.p2p.wifidirectdemo.peers

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.*
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import org.thaliproject.p2p.wifidirectdemo.peers.service.DemoService
import timber.log.Timber

class WifiPeersDiscoverer(val ctx: Context, val wifiP2PManager: WifiP2pManager, val channel: WifiP2pManager.Channel, val peerListener: OnPeerListener) {

    fun discoverSpecialService() {
        registerOurService()
        wifiP2PManager.setDnsSdResponseListeners(channel, object : DnsSdServiceResponseListener {
            override fun onDnsSdServiceAvailable(instanceName: String?, registrationType: String?, srcDevice: WifiP2pDevice?) {
                //TODO add to peer adapter
                Timber.d("device X = " + srcDevice?.deviceName)
                if (DemoService.SERVICE_NAME.equals(instanceName)) {
                    Timber.d("device  Y = " + srcDevice?.deviceName)
                    if (srcDevice != null) {
                        peerListener.onDiscovered(srcDevice)
                    } else {
                        throw IllegalArgumentException("srcDevice is null")
                    }
                }
            }
        }, object : DnsSdTxtRecordListener {
            override fun onDnsSdTxtRecordAvailable(fullDomainName: String?, txtRecordMap: MutableMap<String, String>?, srcDevice: WifiP2pDevice?) {
                Timber.d("device = " + srcDevice?.deviceName)
            }
        })
        val serviceRequest = WifiP2pDnsSdServiceRequest.newInstance(DemoService.SERVICE_REG_TYPE)
        wifiP2PManager.addServiceRequest(channel, serviceRequest, object : ActionListener {
            override fun onSuccess() {
                wifiP2PManager.discoverServices(channel, object : ActionListener {
                    override fun onSuccess() {
                        Timber.d("!")
                    }

                    override fun onFailure(reason: Int) {
                        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
            }

            override fun onFailure(reason: Int) {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }

    private fun registerOurService() {
        wifiP2PManager.addLocalService(channel, DemoService().provideDnsServiceInfo(), object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Timber.d("onSuccess !")
            }

            override fun onFailure(reason: Int) {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }


    interface OnPeerListener {
        fun onDiscovered(peer: WifiP2pDevice)
    }

}