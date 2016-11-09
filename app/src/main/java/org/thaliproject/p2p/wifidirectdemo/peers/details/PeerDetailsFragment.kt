package org.thaliproject.p2p.wifidirectdemo.peers.details

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.thaliproject.p2p.wifidirectdemo.DataTransferService
import org.thaliproject.p2p.wifidirectdemo.R
import org.thaliproject.p2p.wifidirectdemo.WDApplication
import timber.log.Timber

class PeerDetailsFragment : Fragment() {

    companion object {

        private val DEVICE_NAME_KEY = "DEVICE_NAME"
        private val DEVICE_ADDRESS_KEY = "DEVICE_ADDRESS"

        fun newInstance(deviceName: String, deviceAddress: String): PeerDetailsFragment {
            val args = Bundle()
            args.putString(DEVICE_NAME_KEY, deviceName)
            args.putString(DEVICE_ADDRESS_KEY, deviceAddress)
            val fragment = PeerDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var deviceName: String
    private lateinit var deviceAddress: String
    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var address: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceName = arguments.getString(DEVICE_NAME_KEY)
        deviceAddress = arguments.getString(DEVICE_ADDRESS_KEY)
        getWifiManager()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater?.inflate(R.layout.fragment_peer_details, container, false)
        (v!!.findViewById(R.id.peer_details_tv_name) as TextView).text = deviceName
        v.findViewById(R.id.peer_details_btn_connect).setOnClickListener { connect() }
        v.findViewById(R.id.peer_details_btn_send_data).setOnClickListener { sendData() }
        return v;
    }

    private fun connect() {
        Timber.d("connect")
        wifiP2pManager.connect(channel, getConfig(), object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Timber.d("Connected")

                val connManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val nInfo = connManager.activeNetworkInfo
                if (nInfo.isConnected) {

                    wifiP2pManager.requestConnectionInfo(channel, object : WifiP2pManager.ConnectionInfoListener {
                        override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
                            Timber.d("onConnectionInfoAvailable")


                            if (info?.groupOwnerAddress?.hostAddress != null) {
                                Timber.d("onConnectionInfoAvailable hostAddress")
                                this@PeerDetailsFragment.address = info!!.groupOwnerAddress.hostAddress
                            }
                        }
                    })

                }
            }

            override fun onFailure(reason: Int) {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun getConfig(): WifiP2pConfig {
        val config = WifiP2pConfig()
        config.deviceAddress = deviceAddress
        config.wps.setup = WpsInfo.PBC
        return config
    }

    private fun getWifiManager() {
        wifiP2pManager = (activity.application as WDApplication).wifiP2pManager
        channel = (activity.application as WDApplication).channel
    }

    private fun sendData() {
        val startIntent = Intent(activity, DataTransferService::class.java)
        startIntent.action = DataTransferService.ACTION_SEND_DATA
        startIntent.putExtra(DataTransferService.GO_ADDRESS, address)
//        startIntent.putExtra(DataTransferService.GO_g)
        activity.startService(startIntent)
    }

}