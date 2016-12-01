package org.thaliproject.p2p.wifidirectdemo.peers.details

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.SupplicantState
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import org.thaliproject.p2p.wifidirectdemo.BaseFragment
import org.thaliproject.p2p.wifidirectdemo.DefaultActionListener
import org.thaliproject.p2p.wifidirectdemo.R
import org.thaliproject.p2p.wifidirectdemo.service.GroupSettings
import org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery.GetIpsAsyncTask
import org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery.GroupIpAddressesListener
import org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery.GroupIpsInfo
import org.thaliproject.p2p.wifidirectdemo.service.messaging.*
import timber.log.Timber
import java.util.concurrent.ThreadPoolExecutor

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

    private lateinit var btnConnectToAP: Button
    private lateinit var btnSendMulticast: Button

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

        v.findViewById(R.id.peer_details_btn_get_group_info).setOnClickListener { getGroupInfo() }
        btnConnectToAP = v.findViewById(R.id.peer_details_btn_connect_to_ap) as Button
        btnConnectToAP.setOnClickListener { connectToAP() }
        btnSendMulticast = v.findViewById(R.id.peer_details_btn_send_multicast) as Button
        btnSendMulticast.setOnClickListener { sendMulticast() }

        disableSendData()
        disableMulticast()
        enableConnect()
        return v;
    }

    private fun connect() {
        Timber.d("connect")
        wifiDirectState.addConnectionInfoListener(WifiP2pManager.ConnectionInfoListener {
            info ->
            processGroupInfo(info)
        })
        wifiDirectState.connectTo(deviceAddress, object : DefaultActionListener("Connected to $deviceAddress!", "Not connected to $deviceAddress") {
            override fun onSuccess() {
                super.onSuccess()
//                wifiDirectState.wifiDirectInfo.wifiP2pManager.requestConnectionInfo(wifiDirectState.wifiDirectInfo.channel, {
//                    info ->
//                    processGroupInfo(info)
//                })
            }
        })
    }

    private fun processGroupInfo(info: WifiP2pInfo) {
        Timber.d(" Listener Connection info: $info")
//        if (info.groupOwnerAddress != null) {
        groupOwnerAddress = info.groupOwnerAddress.hostAddress
        disableConnect()
        getIps(object : GroupIpAddressesListener {
            override fun onGroupIpAddressesReceived(ipAddresses: List<String>) {
                app.groupIpsInfo = GroupIpsInfo(groupOwnerAddress, ipAddresses)
                startMessagingServer()
                enableSendData()
            }
        })
        //TODO remove listener
//            wifiDirectState.removeConnectionInfoListener()
//        }
    }

    private fun getGroupInfo() {
        wifiDirectState.getGroupInfo(WifiP2pManager.GroupInfoListener { group -> Timber.d("Group info $group") })
    }

    private fun connectToAP(): Boolean {
        //TODO need to create broadcast receiver on wifi events
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = "\"${GroupSettings.GROUP_SSID}\""
        wifiConfig.preSharedKey = "\"${GroupSettings.GROUP_PASSWORD}\""

        registerWifiStateChangedReceiver()

        val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager
        //remember id
        val netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        return wifiManager.reconnect();
    }


    val wifiStateChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
//            Timber.d("Intent ${intent?.action}")
            if (intent?.action == WifiManager.SUPPLICANT_STATE_CHANGED_ACTION) {
                val state = intent?.getParcelableExtra<SupplicantState>(WifiManager.EXTRA_NEW_STATE)
//                Timber.d("onReceive: state  $state")
                if (state == SupplicantState.COMPLETED) {
                    val connectionInfo = (context?.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo
                    val ssid = connectionInfo?.ssid
//                    Timber.d("onReceive: ssid  $ssid")
                    if (ssid == "\"${GroupSettings.GROUP_SSID}\"") {
                        onConnectedToAP()
                    }
                }
            }
        }
    }

    private fun registerWifiStateChangedReceiver() {
        activity.registerReceiver(wifiStateChangedReceiver, IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION))
    }

    private fun onConnectedToAP() {
        activity.unregisterReceiver(wifiStateChangedReceiver)
        Timber.d("Connected to AP! Can send multicast")
        startMulticastsListening()
        enableMulticast()
    }

    private fun startMulticastsListening() {
        val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager
        Thread(MulticastMessageListener(wifiManager)).start()
    }

    private fun sendMulticast() {
        Thread(SendMulticastRunnable(Message.PING)).start()
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

    private fun enableMulticast() {
        btnConnectToAP.visibility = View.INVISIBLE
        btnSendMulticast.visibility = View.VISIBLE
    }

    private fun disableMulticast() {
        btnConnectToAP.visibility = View.VISIBLE
        btnSendMulticast.visibility = View.INVISIBLE
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

    private fun getIps(listener: GroupIpAddressesListener) {
        Timber.d("Get Ips")
        val x = GetIpsAsyncTask(wifiDirectState.wifiDirectInfo, groupOwnerAddress, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    private fun startMessagingServer() {
        //TODO make it just thread, because we can't execute any other async task while executing this one
        MessagingServer(app.applicationContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    private fun sendToAllPeers(message: Message) {
        Timber.d("sendToAllPeers")
        getIps(object : GroupIpAddressesListener {
            override fun onGroupIpAddressesReceived(ipAddresses: List<String>) {
                Timber.d("onGroupIpAddressesReceived ${ipAddresses}")
                app.groupIpsInfo = GroupIpsInfo(groupOwnerAddress, ipAddresses)
                for (address in app.groupIpsInfo.peers) {
                    if (address != wifiDirectState.wifiDirectInfo.ipInfo.currentIp) { //TODO move to groupIpInfo?
                        Timber.d("send to  $address")
                        Thread(SendMessageRunnable(wifiDirectState.wifiDirectInfo, address, message)).start()
                    }
                }
            }
        })
    }

}