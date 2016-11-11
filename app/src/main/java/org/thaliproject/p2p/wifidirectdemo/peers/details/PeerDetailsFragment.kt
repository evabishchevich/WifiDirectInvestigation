package org.thaliproject.p2p.wifidirectdemo.peers.details

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.thaliproject.p2p.wifidirectdemo.*
import timber.log.Timber

class PeerDetailsFragment : BaseFragment() {

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
    private lateinit var groupOwnerAddress: String

    private lateinit var groupIps: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceName = arguments.getString(DEVICE_NAME_KEY)
        deviceAddress = arguments.getString(DEVICE_ADDRESS_KEY)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater?.inflate(R.layout.fragment_peer_details, container, false)
        (v!!.findViewById(R.id.peer_details_tv_name) as TextView).text = deviceName
        v.findViewById(R.id.peer_details_btn_connect).setOnClickListener { connect() }
        v.findViewById(R.id.peer_details_btn_disconnect).setOnClickListener { disconnect() }
        v.findViewById(R.id.peer_details_btn_send_data).setOnClickListener { sendData() }
        return v;
    }

    private fun disconnect() {
        Timber.d("connect")
        wifiDirectState.addConnectionInfoListener(WifiP2pManager.ConnectionInfoListener {
            info ->
            Timber.d(" Listener Connection info: $info")
        })
        wifiDirectState.wifiDirectInfo.wifiP2pManager.removeGroup(wifiDirectState.wifiDirectInfo.channel,
                DefaultActionListener("Group removed successfully!", "Group deletion failed!"))
    }

    private fun connect() {
        Timber.d("connect")
        wifiDirectState.addConnectionInfoListener(WifiP2pManager.ConnectionInfoListener {
            info ->
            Timber.d(" Listener Connection info: $info")
            groupOwnerAddress = info.groupOwnerAddress.hostAddress
        })
        wifiDirectState.connectTo(deviceAddress, DefaultActionListener("Connected to $deviceAddress!", "Not connected to $deviceAddress"))
    }

    private fun getGroupInfo() {
        wifiDirectState.getGroupInfo(object : WifiP2pManager.GroupInfoListener {
            override fun onGroupInfoAvailable(group: WifiP2pGroup?) {
                if (group != null) {
                    groupIps = group.clientList.map { it -> it.deviceAddress }
                } else {
                    throw IllegalArgumentException("empty group info")
                }
            }
        })
    }

    private fun sendData() {
//        val startIntent = Intent(activity, DataTransferService::class.java)
//        startIntent.action = DataTransferService.ACTION_SEND_DATA
//        startIntent.putExtra(DataTransferService.GO_ADDRESS, groupOwnerAddress)
//        startIntent.putExtra(DataTransferService.GO_g)

//        activity.startService(startIntent)
        ClientAsyncTask(wifiDirectState.wifiDirectInfo, groupOwnerAddress).execute()
    }

}