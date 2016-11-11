package org.thaliproject.p2p.wifidirectdemo.service.ipdiscovery

import android.os.AsyncTask
import org.thaliproject.p2p.wifidirectdemo.IpInfo
import org.thaliproject.p2p.wifidirectdemo.WifiDirectInfo
import org.thaliproject.p2p.wifidirectdemo.service.GroupSettings
import org.thaliproject.p2p.wifidirectdemo.service.messaging.Message
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket

class GetIpsAsyncTask(val wifiDirectInfo: WifiDirectInfo, val groupOwnerAddress: String, val listener: GroupIpAddressesListener) :
        AsyncTask<Void, Void, List<String>>() {

    override fun doInBackground(vararg params: Void?): List<String> {
        val port = GroupSettings.GROUP_IPS_PROVIDER_PORT
        val socket = Socket()
        try {
            socket.connect(InetSocketAddress(groupOwnerAddress, port), GroupSettings.CONNECTION_TIMEOUT)
            Timber.d("connected to$groupOwnerAddress")
            wifiDirectInfo.ipInfo = IpInfo(socket.localAddress.hostAddress)
            Timber.d("current ip : ${socket.localAddress.hostAddress}")
            socket.outputStream.write(Message.GET_IPS.data.toByteArray())
            Timber.d("${Message.GET_IPS.data} was written")
            return readData(socket.inputStream)
        } finally {
            socket.close()
        }
    }

    override fun onPostExecute(result: List<String>) {
        listener.onGroupIpAddressesReceived(result)
    }

    private fun readData(from: InputStream): List<String> {
        Timber.d("read data")
        val reader = BufferedReader(InputStreamReader(from))
        val ips = mutableListOf<String>()
        var line: String? = null;
        while ({ line = reader.readLine(); line }() != null) {
            ips.add(line!!)
        }
        ips.forEach { Timber.d("Ip: $it") }
        return ips.toList()
    }
}