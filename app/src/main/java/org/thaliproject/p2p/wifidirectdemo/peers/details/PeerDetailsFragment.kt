package org.thaliproject.p2p.wifidirectdemo.peers.details

import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import org.thaliproject.p2p.wifidirectdemo.BaseFragment
import org.thaliproject.p2p.wifidirectdemo.DefaultActionListener
import org.thaliproject.p2p.wifidirectdemo.R
import org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery.GetIpsAsyncTask
import org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery.GroupIpAddressesListener
import org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery.GroupIpsInfo
import org.thaliproject.p2p.wifidirectdemo.service.messaging.Message
import org.thaliproject.p2p.wifidirectdemo.service.messaging.MessagingServer
import org.thaliproject.p2p.wifidirectdemo.service.messaging.SendMessageRunnable
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

    private lateinit var btnSendData: Button
    private lateinit var btnConnect: Button
    private lateinit var btnDisconnect: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceName = arguments.getString(DEVICE_NAME_KEY)
        deviceAddress = arguments.getString(DEVICE_ADDRESS_KEY)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater?.inflate(R.layout.fragment_peer_details, container, false)
        (v!!.findViewById(R.id.peer_details_tv_name) as TextView).text = deviceName
        btnConnect = v.findViewById(R.id.peer_details_btn_connect) as Button
        btnConnect.setOnClickListener { connect() }
        btnDisconnect = v.findViewById(R.id.peer_details_btn_disconnect) as Button
        btnDisconnect.setOnClickListener { disconnect() }
        btnSendData = v.findViewById(R.id.peer_details_btn_send_data) as Button
        val msg = if (System.currentTimeMillis() % 2 == 0L) Message.HELLO else Message.HI
        btnSendData.setOnClickListener { sendToAllPeers(msg) }

        disableSendData()
        enableConnect()
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
            disableConnect()
            getIps()
        })
        wifiDirectState.connectTo(deviceAddress, DefaultActionListener("Connected to $deviceAddress!", "Not connected to $deviceAddress"))
    }

    private fun enableSendData() {
        btnSendData.visibility = View.VISIBLE
    }

    private fun disableSendData() {
        btnSendData.visibility = View.INVISIBLE
    }

    private fun enableConnect() {
        btnConnect.visibility = View.VISIBLE
        btnDisconnect.visibility = View.INVISIBLE
    }

    private fun disableConnect() {
        btnConnect.visibility = View.INVISIBLE
        btnDisconnect.visibility = View.VISIBLE
    }

    private fun getIps() {
        Timber.d("Get Ips")
        GetIpsAsyncTask(wifiDirectState.wifiDirectInfo, groupOwnerAddress, object : GroupIpAddressesListener {
            override fun onGroupIpAddressesReceived(ipAddresses: List<String>) {
                app.groupIpsInfo = GroupIpsInfo(groupOwnerAddress, ipAddresses)
                startMessagingServer()
                enableSendData()
            }
        }).execute()
    }

    private fun startMessagingServer() {
        MessagingServer(app.applicationContext).execute()
    }

    private fun sendToAllPeers(message: Message) {
        //TODO update peers before sending message
        for (address in app.groupIpsInfo.peers) {
            if (address != wifiDirectState.wifiDirectInfo.ipInfo.currentIp) { //TODO move to groupIpInfo?
                Thread(SendMessageRunnable(wifiDirectState.wifiDirectInfo, address, message)).start()
            }
        }
    }

}