package org.thaliproject.p2p.wifidirectdemo.peers.details

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.SupplicantState
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import org.thaliproject.p2p.wifidirectdemo.BaseFragment
import org.thaliproject.p2p.wifidirectdemo.R
import org.thaliproject.p2p.wifidirectdemo.service.GroupSettings
import org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery.GetIpsAsyncTask
import org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery.GroupIpAddressesListener
import org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery.GroupIpsInfo
import org.thaliproject.p2p.wifidirectdemo.service.messaging.*
import timber.log.Timber

class RegularAPDetailsFragment : BaseFragment() {

    companion object {

        private val SSID_KEY = "SSID_KEY"

        fun newInstance(ssid: String): RegularAPDetailsFragment {
            val args = Bundle()
            args.putString(SSID_KEY, ssid)
            val fragment = RegularAPDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var ssid: String

    private lateinit var btnSendData: Button
    private lateinit var btnConnect: Button
    private lateinit var btnDisconnect: Button
    private lateinit var btnSendMulticast: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ssid = arguments.getString(SSID_KEY)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater?.inflate(R.layout.fragment_ap_details, container, false)
        (v!!.findViewById(R.id.peer_details_tv_name) as TextView).text = ssid
        btnConnect = v.findViewById(R.id.peer_details_btn_connect) as Button
        btnConnect.setOnClickListener { connectToAP() }
        btnDisconnect = v.findViewById(R.id.peer_details_btn_disconnect) as Button
        btnDisconnect.setOnClickListener { disconnect() }
        btnSendData = v.findViewById(R.id.peer_details_btn_send_data) as Button
        val msg = if (System.currentTimeMillis() % 2 == 0L) Message.HELLO else Message.HI
//        btnSendData.setOnClickListener { sendToAllPeers(msg) }

        btnSendMulticast = v.findViewById(R.id.peer_details_btn_send_multicast) as Button
        btnSendMulticast.setOnClickListener { sendMulticast() }

        disableSendData()
        disableMulticast()
        enableConnect()
        return v;
    }

    private fun connectToAP(): Boolean {
        //TODO need to create broadcast receiver on wifi events
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = "\"$ssid\""
        wifiConfig.preSharedKey = "\"${GroupSettings.GROUP_PASSWORD}\""
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        registerWifiStateChangedReceiver()

        val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager
        //remember id
        val netId = wifiManager.addNetwork(wifiConfig);
        Timber.d("Disconnected: ${wifiManager.disconnect()}")
        Timber.d("Enabled: ${wifiManager.enableNetwork(netId, true)}")
//        Timber.d("Config is saved: ${wifiManager.saveConfiguration()}")
        val reconnected = wifiManager.reconnect()
        Timber.d("Reconnected: $reconnected")
        return reconnected
    }


    val wifiStateChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("Intent ${intent?.action}")
            if (intent?.action == WifiManager.SUPPLICANT_STATE_CHANGED_ACTION ||
                    intent?.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                val state = intent?.getParcelableExtra<SupplicantState>(WifiManager.EXTRA_NEW_STATE)
                Timber.d("onReceive: state  $state")
                if (state == SupplicantState.COMPLETED) {
                    getSSID()
                }
            } else {
                val netInfo = intent?.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                Timber.d("onReceive: netInfo  $netInfo")
                if (netInfo != null && netInfo.isConnected) {
                    getSSID()
                }
            }
        }
    }

    private fun getSSID() {
        val connectionInfo = (context?.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo
        val ssid = connectionInfo?.ssid
        Timber.d("onReceive: ssid  $ssid")
        informAboutSSID(ssid)

    }

    private fun informAboutSSID(ssid: String?) {
        if (ssid == "\"${this.ssid}\"") {
            onConnectedToAP()
        }
    }

    private fun registerWifiStateChangedReceiver() {
        val intentFilter = IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        activity.registerReceiver(wifiStateChangedReceiver, intentFilter)
    }

    private fun onConnectedToAP() {
        activity.unregisterReceiver(wifiStateChangedReceiver)
        Timber.d("Connected to AP! Can send multicast")
        startMulticastsListening()
        enableMulticast()
//        startMessagingServer()
        disableConnect()
//        enableSendData()
    }

    private fun startMulticastsListening() {
        val wifiManager = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager
        Thread(MulticastMessageListener(wifiManager, false)).start()
    }

    private fun sendMulticast() {
        Thread(SendMulticastRunnable(Message.PING)).start()
    }

    private fun disconnect() {
        //TODO implement disconnect
    }

    private fun enableMulticast() {
        btnSendMulticast.visibility = View.VISIBLE
    }

    private fun disableMulticast() {
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

//    private fun getIps(listener: GroupIpAddressesListener) {
//        Timber.d("Get Ips")
//        val x = GetIpsAsyncTask(wifiDirectState.wifiDirectInfo, groupOwnerAddress, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
//    }

    private fun startMessagingServer() {
        //TODO make it just thread, because we can't execute any other async task while executing this one
        MessagingServer(app.applicationContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

//    private fun sendToAllPeers(message: Message) {
//        Timber.d("sendToAllPeers")
//        getIps(object : GroupIpAddressesListener {
//            override fun onGroupIpAddressesReceived(ipAddresses: List<String>) {
//                Timber.d("onGroupIpAddressesReceived ${ipAddresses}")
//                app.groupIpsInfo = GroupIpsInfo(groupOwnerAddress, ipAddresses)
//                for (address in app.groupIpsInfo.peers) {
//                    if (address != wifiDirectState.wifiDirectInfo.ipInfo.currentIp) { //TODO move to groupIpInfo?
//                        Timber.d("send to  $address")
//                        Thread(SendMessageRunnable(wifiDirectState.wifiDirectInfo, address, message)).start()
//                    }
//                }
//            }
//        })
//    }


}